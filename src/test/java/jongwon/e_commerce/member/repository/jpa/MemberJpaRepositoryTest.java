package jongwon.e_commerce.member.repository.jpa;

import jongwon.e_commerce.member.repository.jpa.entity.MemberEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = true)
@TestPropertySource("classpath:application-test.properties")
@Sql(value = "/sql/member-repository-test-data.sql")
class MemberJpaRepositoryTest {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Test
    void findById_로_유저_데이터를_찾아올_수_있다(){
        // given
        // when
        MemberEntity resultEntity = memberJpaRepository.findById(1L).orElseThrow();

        // then
        assertThat(resultEntity).isNotNull();
        assertThat(resultEntity.getMemberId()).isEqualTo(1L);
        assertThat(resultEntity.getLoginId()).isEqualTo("testUser");
        assertThat(resultEntity.getPassword()).isEqualTo("password123");
        assertThat(resultEntity.getMemberName()).isEqualTo("gildong");
        assertThat(resultEntity.getEmail()).isEqualTo("test@example.com");
        assertThat(resultEntity.getAddr()).isEqualTo("seoul");
    }

    @Test
    void findById_는_데이터가_없으면_Optional_empty를_내려준다(){
        // given
        // when
        Optional<MemberEntity> result = memberJpaRepository.findById(2L);

        // then
        assertThat(result.isEmpty()).isTrue();
    }

}