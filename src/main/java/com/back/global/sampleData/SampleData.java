package com.back.global.sampleData;

import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

/**
 * 앱이 시작될 때 샘플데이터(글 3개, 댓글 5개)를 자동으로 만드는 설정 클래스입니다.
 * 개발 중에 데이터를 손으로 넣지 않아도 바로 API를 호출해 볼 수 있게 해 줍니다.
 *
 * <p>테스트(test 프로필)에서도 똑같이 실행되므로, 테스트는 "글 1~3번, 댓글 1~5번이 있다"는 전제로 작성되어 있습니다.
 */
@Configuration
@RequiredArgsConstructor
public class SampleData {
    /**
     * 자기 자신(SampleData 빈)을 주입받는 필드. 이른바 "self 주입" 패턴입니다.
     *
     * <p><b>왜 필요할까?</b>
     * <br>{@code @Transactional}은 스프링이 만든 <b>프록시 객체</b>를 거쳐서 호출될 때만 동작합니다.
     * 같은 클래스 안에서 {@code this.makeSampleData()}로 부르면 프록시를 건너뛰기 때문에
     * 트랜잭션이 시작되지 않습니다. 그러면 cascade PERSIST로 댓글을 저장하는 변경 감지가 동작하지 않습니다.
     * 그래서 프록시인 {@code self}를 통해 호출합니다.
     *
     * <p><b>{@code @Lazy}</b>: 자기 자신을 생성 도중에 주입하려고 하면 순환 참조 오류가 납니다.
     * {@code @Lazy}를 붙이면 일단 가짜(지연 프록시)를 넣어 두고, 실제로 쓰는 시점에 진짜 빈을 찾아 연결합니다.
     */
    @Autowired
    @Lazy
    private SampleData self;
    private final PostService postService;

    /**
     * {@link ApplicationRunner}는 스프링 부트가 앱을 완전히 띄운 직후 한 번 실행해 주는 콜백입니다.
     * {@code @Bean}으로 등록만 해 두면 부트가 알아서 찾아서 실행합니다.
     */
    @Bean
    ApplicationRunner sampleDataApplicationRunner() {
        return args -> {
            self.makeSampleData(); // this가 아니라 self(프록시)로 호출 → @Transactional 적용
        };
    }

    /**
     * 글이 하나도 없을 때만 샘플데이터를 만듭니다.
     *
     * <p>dev 프로필은 파일 DB(db_dev)를 쓰고 {@code ddl-auto: update}라서 재시작해도 데이터가 남습니다.
     * 맨 앞의 개수 체크가 없으면 재시작할 때마다 글이 3개씩 늘어납니다.
     *
     * <p>댓글은 {@code post1.addComment(...)}만 호출하고 따로 save하지 않습니다.
     * 트랜잭션이 끝날 때 JPA가 글의 comments 리스트에 새 댓글이 추가된 것을 감지하고,
     * cascade PERSIST 설정에 따라 댓글을 insert합니다.
     */
    @Transactional
    public void makeSampleData() {
        if (postService.count() > 0) return;

        Post post1 = postService.write("제목 1", "내용 1");
        Post post2 = postService.write("제목 2", "내용 2");
        postService.write("제목 3", "내용 3");

        post1.addComment("댓글 1-1");
        post1.addComment("댓글 1-2");
        post1.addComment("댓글 1-3");
        post2.addComment("댓글 2-1");
        post2.addComment("댓글 2-2");
    }
}
