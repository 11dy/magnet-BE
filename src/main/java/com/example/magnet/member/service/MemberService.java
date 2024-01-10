package com.example.magnet.member.service;

import com.example.magnet.global.auth.utils.CustomAuthorityUtils;
import com.example.magnet.global.exception.BusinessLogicException;
import com.example.magnet.global.exception.ExceptionCode;
import com.example.magnet.global.helper.event.MemberRegistrationApplicationEvent;
import com.example.magnet.member.dto.MemberResponseDto;
import com.example.magnet.member.entity.Address;
import com.example.magnet.member.entity.Member;
import com.example.magnet.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher publisher;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final PasswordEncoder passwordEncoder;
    private final CustomAuthorityUtils authorityUtils;

    /**
     *  회원가입
     * - 이메일 기반으로 회원이 db에 존재하는지 판단 후 spring security의 createRoles를 통해 역할 생성
     * */
    public void createMember(Member member) {
        verifyExistsEmail(member.getEmail());

        String encryptedPassword = passwordEncoder.encode(member.getPassword()); //db에 저장되는 password 암호화
        List<String> roles = authorityUtils.createRoles(member.getEmail());// 사용자 이메일 기반으로 사용자 역할을 생성, 저장

        Member.MemberBuilder builder = member.toBuilder();
        builder.password(encryptedPassword);
        builder.roles(roles);
        builder.memberStatus(Member.MemberStatus.MEMBER_ACTIVE);

        Member savedMember = memberRepository.save(builder.build());

        // 회원가입 이벤트 객체 생성
        publisher.publishEvent(new MemberRegistrationApplicationEvent(savedMember));
    }

    public void verifyExistsEmail(String email){
        Optional<Member> member = memberRepository.findByEmail(email);
        if(member.isPresent())
            throw new BusinessLogicException(ExceptionCode.MEMBER_EXISTS);
    }

    /**
     * Redis 기반의 로그아웃
     * */


    //회원 수정
    public void updateMember(Member member) {

        Member findMember = memberRepository.findById(member.getId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        // entity에 매개변수로 전달받은 데이터 삽입. 변경된 내용만 적용
        Member.MemberBuilder builder = findMember.toBuilder(); // 새로운 객체 생성

        Optional.ofNullable(member.getNickName())
                .ifPresent(nickName -> builder.nickName(member.getNickName()));

        Optional.ofNullable(member.getPhone())
                .ifPresent(phone -> builder.phone(member.getPhone()));

        if (member.getAddress() != null) { // 주소 부분 수정 고도화
            Address.AddressBuilder addressBuilder = Address.builder();

            Optional.ofNullable(member.getAddress().getCity())
                    .ifPresent(city -> addressBuilder.city(city));

            Optional.ofNullable(member.getAddress().getStreet())
                    .ifPresent(street -> addressBuilder.street(street));

            builder.address(addressBuilder.build());
        }

        memberRepository.save(builder.build());
    }

    // 엔티티는 비즈니스 레이어에서만 조작 > dto의 변환은 controller에서
    @Transactional(readOnly = true)
    public Member findMyInfo(Long memberId) {
        Member findMember = memberRepository.findByIdWithMenteeMentorMentoring(memberId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        return findMember;
    }

    // jwt의 memberId와 생성자가 같은지 판단하는 함수


}
