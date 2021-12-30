package org.egovframe.cloud.portalservice.domain.menu;

import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.egovframe.cloud.portalservice.api.menu.dto.MenuRoleResponseDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableConfigurationProperties
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@ActiveProfiles(profiles = "test")
class MenuRoleRepositoryTest {
    @Autowired
    private EntityManager em;

    @Autowired
    MenuRepository menuRepository;

    @Autowired
    SiteRepository siteRepository;

    @Autowired
    MenuRoleRepository menuRoleRepository;

    @BeforeEach
    public void setup() {
        //given
        Site site = Site.builder().name("site1").isUse(true).build();
        siteRepository.save(site);


    }

    @AfterEach
    public void tearDown() {
        siteRepository.deleteAll();
        menuRepository.deleteAll();
        menuRoleRepository.deleteAll();
    }

    @Test
    @Transactional
    public void 권한별메뉴_트리_조회한다() throws Exception {
        Site site = siteRepository.findAll().get(0);
        //given
        Menu parent = Menu.builder()
                .menuKorName("parent")
                .sortSeq(1)
                .site(site)
                .isShow(true)
                .isUse(true)
                .level(1)
                .parent(null)
                .build();

        em.persist(parent);
        em.flush();

        //when
        for (int i = 0; i < 3; i++) {
            Menu child = Menu.builder()
                    .menuKorName("child_" + i)
                    .site(site)
                    .parent(Optional.of(parent))
                    .sortSeq(i + 1)
                    .level(2)
                    .build();
            child.setParentMenu(parent);
            em.persist(child);
        }
        em.flush();

        List<MenuRoleResponseDto> tree = menuRoleRepository.findTree("role", site.getId());

        Assertions.assertThat(tree.size()).isEqualTo(1);
        Assertions.assertThat(tree.get(0).getChildren().size()).isEqualTo(3);
        tree.stream().forEach(menuRoleResponseDto -> {
            System.out.println(menuRoleResponseDto);
            menuRoleResponseDto.getChildren().stream().forEach(System.out::println);
        });
    }


}