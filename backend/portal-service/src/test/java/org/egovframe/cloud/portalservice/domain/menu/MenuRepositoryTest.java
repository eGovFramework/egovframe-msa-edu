package org.egovframe.cloud.portalservice.domain.menu;

import java.util.Optional;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableConfigurationProperties
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@ActiveProfiles(profiles = "test")
class MenuRepositoryTest {

    @Autowired
    private EntityManager em;

    @Autowired
    MenuRepository menuRepository;

    @Autowired
    SiteRepository siteRepository;

    @BeforeEach
    public void setup() {
        //given
        Site site1 = Site.builder().name("site1").isUse(true).build();
        Site site2 = Site.builder().name("site2").isUse(true).build();
        List<Site> sites = new ArrayList<>();
        sites.add(site1);
        sites.add(site2);
        siteRepository.saveAll(sites);
    }

    @AfterEach
    public void tearDown() {
        menuRepository.deleteAll();
        siteRepository.deleteAll();
    }

    @Test
    public void 새로운메뉴_한건_등록() throws Exception {
        Site site = siteRepository.findAll().get(0);

        Menu menu = Menu.builder()
                .menuKorName("testMenu")
                .sortSeq(1)
                .parent(null)
                .site(site)
                .build();

        //when
        Menu savedMenu = menuRepository.save(menu);

        //then
        assertThat(savedMenu.getMenuKorName()).isEqualTo(menu.getMenuKorName());
        System.out.println(savedMenu.toString());
    }
    
    @Test
    @Transactional
    public void 새로운_하위메뉴여러개_등록() throws Exception {
        //given
        Site site = siteRepository.findAll().get(0);

        Menu parentMenu = Menu.builder()
                .menuKorName("parent")
                .sortSeq(1)
                .site(site)
                .parent(null)
                .build();

        em.persist(parentMenu);
        em.flush();

        //when
        List<Menu> menus = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Menu child = Menu.builder()
                    .menuKorName("child_" + i)
                    .site(site)
                    .parent(Optional.of(parentMenu))
                    .sortSeq(i + 1)
                    .build();
            child.setParentMenu(parentMenu);
            menus.add(child);
        }

        List<Menu> savedMenus = menuRepository.saveAll(menus);

        //then
        assertThat(savedMenus.size()).isEqualTo(3);
        savedMenus.stream().forEach(System.out::println);
        assertThat(savedMenus.get(0).getParent().getId()).isEqualTo(parentMenu.getId());
        assertThat(savedMenus.get(0).getParent()).isSameAs(parentMenu);

    }

    @Test
    @Transactional
    public void 계층구조_메뉴_조회() throws Exception {
        //given
        Site site = siteRepository.findAll().get(0);

        Menu parentMenu = Menu.builder()
                .menuKorName("parent")
                .sortSeq(1)
                .site(site)
                .build();
        em.persist(parentMenu);
        em.flush();

        for (int i = 0; i < 3; i++) {
            Menu childMenu = Menu.builder()
                    .menuKorName("child_" + i)
                    .site(site)
                    .parent(Optional.of(parentMenu))
                    .sortSeq(i + 1)
                    .build();
            childMenu.setParentMenu(parentMenu);
            em.persist(childMenu);
        }
        em.flush();

        Menu menu = menuRepository.findById(parentMenu.getId()).get();

        menu.getChildren().stream().forEach(System.out::println);
        assertThat(menu.getChildren().size()).isEqualTo(3);

    }

}