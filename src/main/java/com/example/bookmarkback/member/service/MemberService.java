package com.example.bookmarkback.member.service;

import com.example.bookmarkback.auth.repository.RefreshTokenRepository;
import com.example.bookmarkback.book.entity.BookLog;
import com.example.bookmarkback.book.entity.BookLogQuestion;
import com.example.bookmarkback.book.entity.BookRecord;
import com.example.bookmarkback.book.repository.BookLogQuestionRepository;
import com.example.bookmarkback.book.repository.BookLogRepository;
import com.example.bookmarkback.book.repository.BookRecordRepository;
import com.example.bookmarkback.global.dto.MemberAuth;
import com.example.bookmarkback.global.exception.BadRequestException;
import com.example.bookmarkback.global.exception.ForbiddenException;
import com.example.bookmarkback.global.exception.ResourceNotFoundException;
import com.example.bookmarkback.member.dto.MemberResponse;
import com.example.bookmarkback.member.entity.Member;
import com.example.bookmarkback.member.repository.MemberRepository;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final BookRecordRepository bookRecordRepository;
    private final BookLogRepository bookLogRepository;
    private final BookLogQuestionRepository bookLogQuestionRepository;

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Transactional(readOnly = true)
    public MemberResponse getMyInfo(MemberAuth memberAuth) {
        Member member = memberRepository.findById(memberAuth.memberId())
                .orElseThrow(() -> new BadRequestException("해당 id의 계정이 존재하지 않습니다."));
        return MemberResponse.response(member);
    }

    @Transactional
    public void logout(MemberAuth memberAuth) {
        Member member = memberRepository.findById(memberAuth.memberId())
                .orElseThrow(() -> new BadRequestException("해당하는 계정이 없습니다."));
        refreshTokenRepository.deleteByMember(member);
    }

    @Transactional
    public void deleteMember(Long id, MemberAuth memberAuth) {
        validateRequest(id, memberAuth);
        deleteProcess(id);
    }

    @Transactional
    public String uploadFile(MultipartFile file, MemberAuth memberAuth) throws IOException {

        Member member = memberRepository.findById(memberAuth.memberId())
                .orElseThrow(() -> new ResourceNotFoundException("사용자가 존재하지 않습니다."));

        String key = "profile/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

        member.setProfileImage(key);

        return key;

    }

    @Transactional(readOnly = true)
    public String getProfileImage(MemberAuth memberAuth) {
        Member member = memberRepository.findById(memberAuth.memberId())
                .orElseThrow(() -> new ResourceNotFoundException("사용자가 존재하지 않습니다."));
        String key = member.getProfileImage();
        if (key == null) {
            return null;
        }

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(
                GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(5))
                        .getObjectRequest(getObjectRequest)
                        .build()
        );

        return presignedRequest.url().toString();
    }

    private void deleteProcess(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("계정 정보가 존재하지 않습니다."));
        refreshTokenRepository.deleteByMember(member);

        List<BookRecord> bookRecords = bookRecordRepository.findByMemberId(memberId);
        for (BookRecord bookRecord : bookRecords) {
            List<BookLog> bookLogs = bookLogRepository.findByBookRecord(bookRecord);
            for (BookLog bookLog : bookLogs) {
                List<BookLogQuestion> bookLogQuestions = bookLogQuestionRepository.findByBookLogId(bookLog.getId());
                for (BookLogQuestion bookLogQuestion : bookLogQuestions) {
                    bookLogQuestionRepository.delete(bookLogQuestion);
                }
                bookLogRepository.delete(bookLog);
            }
            bookRecordRepository.delete(bookRecord);
        }

        memberRepository.delete(member);
    }

    private void validateRequest(Long id, MemberAuth memberAuth) {
        if (!id.equals(memberAuth.memberId())) {
            throw new ForbiddenException("다른 사용자의 계정을 삭제할 수 없습니다.");
        }
    }

}
