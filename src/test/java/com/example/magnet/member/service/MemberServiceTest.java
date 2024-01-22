package com.example.magnet.member.service;

import com.example.magnet.global.auth.utils.CustomAuthorityUtils;
import com.example.magnet.global.exception.BusinessLogicException;
import com.example.magnet.global.helper.event.MemberRegistrationApplicationEvent;
import com.example.magnet.member.entity.Address;
import com.example.magnet.member.entity.Member;
import com.example.magnet.member.repository.MemberRepository;
import com.example.magnet.mentee.entity.Mentee;
import com.example.magnet.mentee.repository.MenteeRepository;
import com.example.magnet.mentor.entity.Mentor;
import com.example.magnet.mentor.repository.MentorRepository;
import com.example.magnet.mentoring.entity.Mentoring;
import com.example.magnet.mentoring.repository.MentoringRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Mock
    MemberRepository memberRepository;
    @Mock
    MentorRepository mentorRepository;
    @Mock
    MentoringRepository mentoringRepository;
    @Mock
    MenteeRepository menteeRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    CustomAuthorityUtils authorityUtils;

    @Mock
    ApplicationEventPublisher publisher;

    @InjectMocks
    MemberService memberService;

    @Mock
    Member member;

    @Mock
    Mentor mentor;
    @Mock
    Mentoring mentoring;
    @Mock
    Mentee mentee;



    @Test
    @DisplayName("회원가입 테스트 - 정상 생성")
    void createMember() {
        // given
        String email = "test@gmail.com";
        String password = "password";
        Member member = Member.builder()
                .email(email)
                .password(password)
                .build();
        String encryptedPassword = "encryptedPassword";
        List<String> roles = List.of("USER");

        // when
        when(passwordEncoder.encode(password)).thenReturn(encryptedPassword);
        when(authorityUtils.createRoles(email)).thenReturn(roles);
        when(memberRepository.save(any())).thenReturn(member);

        memberService.createMember(member);

        // then
        verify(passwordEncoder).encode(password);
        verify(authorityUtils).createRoles(email);
        verify(memberRepository).save(member);
        verify(publisher).publishEvent(any(MemberRegistrationApplicationEvent.class));
    }

    @Test
    @DisplayName("회원 생성 - 중복 이메일 에러")
    void createMember_duplicateEmail() {
        // given
        String email = "test@gmail.com";
        Member existingMember = Member.builder().email(email).build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(existingMember));

        // when, then
        assertThrows(BusinessLogicException.class, () -> memberService.verifyExistsEmail(email));
    }



    @Test
    @DisplayName("회원수정- success")
    void updateMember_modify(){
        // given
        Member existingMember = Member.builder()
                .id(1L)
                .nickName("OldNickName")
                .phone("OldPhone")
                .address(Address.builder().city("OldCity").street("OldStreet").build())
                .build();


        Member updatedMember = Member.builder()
                .id(1L)
                .nickName("NewNickName")
                .phone("NewPhone")
                .address(Address.builder().city("NewCity").street("NewStreet").build())
                .build();

        //when
        when(memberRepository.findById(1L)).thenReturn(Optional.of(existingMember));
        memberService.updateMember(updatedMember);

        //then
        verify(memberRepository).save(updatedMember); // 저장 확인
    }

    @Test
    @DisplayName("회원조회 테스트")
    void getMember(){
        List<String> roles = new ArrayList<>();
        roles.add("USER");

        Member existingMember = Member.builder()
                .id(1L)
                .username("testUser")
                .nickName("OldNickName")
                .phone("O10-0000-0000")
                .picture("domain-of-s3")
                .memberStatus(Member.MemberStatus.MEMBER_ACTIVE)
                .address(Address.builder().city("testCity").street("testStreet").build())
                .roles(roles)
                .menteeList(null)
                .mentoringList(null)
                .mentorList(null)
                .build();


        // when
        when(memberRepository.findById(1L)).thenReturn(Optional.ofNullable(existingMember));

        // then
        Member result = memberService.findMyInfo(1L);

        assertEquals(existingMember, result);
    }

    @Test
    @DisplayName("회원 탈퇴 테스트")
    void deleteMember(){

        // 삭제 후 deleted 필드 true 변경 내용과 연관 mentor, mentoring, mentee 정보 삭제 확인
        Member existingMember = Member.builder()
                .id(1L)
                .username("testUser")
                .nickName("OldNickName")
                .mentorList(new ArrayList<>())
                .menteeList(new ArrayList<>())
                .mentoringList(new ArrayList<>())
                .build();
        memberRepository.save(existingMember);


        Mentor mentor1 = Mentor.builder()
                .member(existingMember)
                .build();

        Mentoring mentoring1 = Mentoring.builder()
                .member(existingMember)
                .build();

        Mentee mentee1 = Mentee.builder()
                .member(existingMember)
                .build();


        // 회원 삭제 메서드 호출
        memberService.deleteMember(1L);

        // 삭제된 회원을 찾아서 해당 회원이 삭제되었음을 확인
        Member deletedMember = memberRepository.findById(1L).orElse(null);
        assertNotNull(deletedMember);
        assertTrue(deletedMember.getDeleted());

//        assertThrows(BusinessLogicException.class, () ->{
//            memberService.deleteMember(1L);
//        });
        // 삭제된 회원과 관련된 멘토, 멘티, 멘토링 정보가 모두 삭제되었음을 확인
        assertTrue(existingMember.getMentorList().isEmpty());
        assertTrue(existingMember.getMenteeList().isEmpty());
        assertTrue(existingMember.getMentoringList().isEmpty());

    }




}