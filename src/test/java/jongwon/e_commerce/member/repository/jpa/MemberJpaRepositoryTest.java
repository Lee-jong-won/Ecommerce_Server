package jongwon.e_commerce.member.repository.jpa;

import jongwon.e_commerce.support.fixture.MemberFixture;
import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.member.repository.jpa.entity.MemberEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = true)
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberJpaRepositoryTest {

    @Autowired
    private MemberJpaRepository memberJpaRepository;
    @Test
    void findById_로_유저_데이터를_찾아올_수_있다(){
        // given
        Member member = MemberFixture.builder().build().create();
        MemberEntity memberEntity = memberJpaRepository.save(MemberEntity.from(member));

        // when
        MemberEntity resultEntity = memberJpaRepository.findById(memberEntity.getMemberId()).orElseThrow();

        // then
        assertThat(resultEntity.getMemberName()).isEqualTo(memberEntity.getMemberName());
        assertThat(resultEntity.getLoginId()).isEqualTo(memberEntity.getLoginId());
        assertThat(resultEntity.getPassword()).isEqualTo(memberEntity.getPassword());
        assertThat(resultEntity.getEmail()).isEqualTo(memberEntity.getEmail());
    }
}