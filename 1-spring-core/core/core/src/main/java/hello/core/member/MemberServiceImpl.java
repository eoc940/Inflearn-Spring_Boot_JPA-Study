package hello.core.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MemberServiceImpl implements MemberService {

    //인터페이스와 구현체 양쪽에 memberRepositoy 의존 -> dip 위반
    private final MemberRepository memberRepository;

    //@Autowired //자동 의존관계 주입. 마치 ac.getBean(MemberRepository.class)를 넣어준 거 같은 효과
    public MemberServiceImpl(MemberRepository memberRepository) {
        System.out.println("??????");
        this.memberRepository = memberRepository;
    }

    @Override
    public void join(Member member) {
        memberRepository.save(member);
    }

    @Override
    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId);
    }

    ///테스트 용도
    public MemberRepository getMemberRepository() {
        return memberRepository;
    }
}
