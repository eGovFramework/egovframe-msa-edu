package org.egovframe.cloud.portalservice.api.menu;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.egovframe.cloud.portalservice.api.menu.dto.MenuDnDRequestDto;
import org.egovframe.cloud.portalservice.api.menu.dto.MenuResponseDto;
import org.egovframe.cloud.portalservice.api.menu.dto.MenuTreeRequestDto;
import org.egovframe.cloud.portalservice.api.menu.dto.MenuTreeResponseDto;
import org.egovframe.cloud.portalservice.api.menu.dto.MenuUpdateRequestDto;
import org.egovframe.cloud.portalservice.api.menu.dto.SiteResponseDto;
import org.egovframe.cloud.portalservice.domain.menu.Menu;
import org.egovframe.cloud.portalservice.domain.menu.MenuRepository;
import org.egovframe.cloud.portalservice.domain.menu.Site;
import org.egovframe.cloud.portalservice.domain.menu.SiteRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableConfigurationProperties
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@ActiveProfiles(profiles = "test")
class MenuApiControllerTest {


    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private SiteRepository siteRepository;


    @BeforeEach
    public void setup() throws Exception {
        siteRepository.save(Site.builder()
                .name("site")
                .isUse(true)
                .build()
        );
    }

    @AfterEach
    public void cleanup() throws Exception {
        menuRepository.deleteAll();
        siteRepository.deleteAll();
    }

    @Test
    public void 메뉴목록을조회한다_bySiteId() throws Exception {
        Site site = siteRepository.findAll().get(0);

        Menu parentMenu = menuRepository.save(Menu.builder()
                .menuKorName("parent")
                .sortSeq(1)
                .site(site)
                .build());

        for (int i = 0; i < 3; i++) {
            Menu childMenu = Menu.builder()
                    .menuKorName("child_" + i)
                    .site(site)
                    .parent(Optional.of(parentMenu))
                    .sortSeq(i + 1)
                    .build();
            childMenu.setParentMenu(parentMenu);
            menuRepository.save(childMenu);
        }

        //when
        ResponseEntity<List<MenuTreeResponseDto>> responseEntity = restTemplate.exchange("/api/v1/menus/"+site.getId()+"/tree", HttpMethod.GET, null, new ParameterizedTypeReference<List<MenuTreeResponseDto>>(){});

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<MenuTreeResponseDto> body = responseEntity.getBody();
        assertThat(body.size()).isEqualTo(1);
        body.stream().forEach(System.out::println);
        assertThat(body.get(0).getChildren().size()).isEqualTo(3);
        body.stream().forEach(menuTreeResponseDto -> {
            menuTreeResponseDto.getChildren().stream().forEach(System.out::println);
        });
    }

    @Test
    public void 메뉴관리_사이트콤보_목록_조회한다() throws Exception {
        //given
        siteRepository.save(Site.builder()
                .name("portal")
                .isUse(true)
                .build()
        );
        siteRepository.save(Site.builder()
                .name("admin")
                .isUse(true)
                .build()
        );

        //when
        ResponseEntity<List<SiteResponseDto>> responseEntity = restTemplate.exchange("/api/v1/sites", HttpMethod.GET, null, new ParameterizedTypeReference<List<SiteResponseDto>>(){});
        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).extracting("name").contains("portal", "admin");
        responseEntity.getBody().stream().forEach(System.out::println);

    }

    @Test
    public void 메뉴관리_왼쪽트리_조회한다() throws Exception {
        //given
        Site site = siteRepository.findAll().get(0);

        Menu parentMenu1 = menuRepository.save(Menu.builder()
                .menuKorName("parent_1")
                .sortSeq(1)
                .site(site)
                .build());
        Menu parentMenu2 = menuRepository.save(Menu.builder()
                .menuKorName("parent_2")
                .sortSeq(2)
                .site(site)
                .build());

        for (int i = 0; i < 3; i++) {
            Menu childMenu1 = Menu.builder()
                    .menuKorName("child_1_" + i)
                    .site(site)
                    .parent(Optional.of(parentMenu1))
                    .sortSeq(i + 1)
                    .build();
            childMenu1.setParentMenu(parentMenu1);
            menuRepository.save(childMenu1);
            if (i == 1) {
                Menu childChildMenu = Menu.builder()
                        .menuKorName("child_child_1")
                        .site(site)
                        .parent(Optional.of(childMenu1))
                        .sortSeq(1)
                        .build();
                childChildMenu.setParentMenu(childMenu1);
                menuRepository.save(childChildMenu);
                Menu childChildMenu2 = Menu.builder()
                        .menuKorName("child_child_1")
                        .site(site)
                        .parent(Optional.of(childMenu1))
                        .sortSeq(2)
                        .build();
                childChildMenu2.setParentMenu(childMenu1);
                menuRepository.save(childChildMenu2);
            }

            Menu childMenu2 = Menu.builder()
                    .menuKorName("child_2_" + i)
                    .site(site)
                    .parent(Optional.of(parentMenu2))
                    .sortSeq(i + 1)
                    .build();
            childMenu1.setParentMenu(parentMenu2);
            menuRepository.save(childMenu2);
        }

        ResponseEntity<List<MenuTreeResponseDto>> responseEntity = restTemplate.exchange("/api/v1/menus/"+site.getId()+"/tree", HttpMethod.GET, null, new ParameterizedTypeReference<List<MenuTreeResponseDto>>(){});
        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<MenuTreeResponseDto> body = responseEntity.getBody();
        assertThat(body).extracting("name").contains(parentMenu1.getMenuKorName(), parentMenu2.getMenuKorName());
        body.stream().forEach(menuTreeResponseDto -> {
            System.out.println(menuTreeResponseDto);
            menuTreeResponseDto.getChildren().stream().forEach(menuTreeResponseDto1 -> {
                System.out.println(menuTreeResponseDto1);
                menuTreeResponseDto1.getChildren().stream().forEach(System.out::println);
            });

        });


    }

    @Test
    public void 메뉴관리_오른쪽_상세정보_조회한다() throws Exception {
        Site site = siteRepository.findAll().get(0);

        Menu parentMenu = menuRepository.save(Menu.builder()
                .menuKorName("parent")
                .menuKorName("parenteng")
                .sortSeq(1)
                .site(site)
                .build());

        //when
        String url = "/api/v1/menus/"+parentMenu.getId();
        ResponseEntity<MenuResponseDto> responseEntity = restTemplate.getForEntity(url, MenuResponseDto.class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).extracting("menuKorName").isEqualTo(parentMenu.getMenuKorName());
        System.out.println(responseEntity.getBody());

    }

    @Test
    public void 메뉴관리_트리메뉴_추가한다() throws Exception {
        //given
        Site site = siteRepository.findAll().get(0);

        MenuTreeRequestDto menuTreeRequestDto = MenuTreeRequestDto.builder()
                .parentId(null)
                .siteId(site.getId())
                .name("parent")
                .sortSeq(1)
                .build();

        String url = "/api/v1/menus";

        //when
        ResponseEntity<MenuTreeResponseDto> responseEntity = restTemplate.postForEntity(url, menuTreeRequestDto, MenuTreeResponseDto.class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        System.out.println(responseEntity.getBody());
        assertThat(responseEntity.getBody()).extracting("name").isEqualTo(menuTreeRequestDto.getName());

    }

    @Test
    public void 메뉴관리_트리_드래그앤드랍_순서_및_부모메뉴_변경() throws Exception {
        //given
        Site site = siteRepository.findAll().get(0);

        Menu parentMenu1 = menuRepository.save(Menu.builder()
                .menuKorName("parent_1")
                .sortSeq(1)
                .site(site)
                .build());
        Menu parentMenu2 = menuRepository.save(Menu.builder()
                .menuKorName("parent_2")
                .sortSeq(2)
                .site(site)
                .build());

        Long menuId = 0L;
        for (int i = 0; i < 3; i++) {
            Menu childMenu1 = Menu.builder()
                    .menuKorName("child_1_" + i)
                    .site(site)
                    .parent(Optional.of(parentMenu1))
                    .sortSeq(i + 1)
                    .build();
            childMenu1.setParentMenu(parentMenu1);
            Menu save = menuRepository.save(childMenu1);
            menuId = save.getId();
        }

        List<MenuDnDRequestDto> updateList = new ArrayList<>();

        updateList.add(MenuDnDRequestDto.builder()
                .menuId(menuId)
                .sortSeq(1)
                .parentId(parentMenu2.getId())
                .build());

        HttpEntity<List<MenuDnDRequestDto>> httpEntity = new HttpEntity<>(
                updateList
        );


        String url = "/api/v1/menus/"+site.getId()+"/tree";

        //when
        ResponseEntity<Long> responseEntity =
                restTemplate.exchange(url, HttpMethod.PUT, httpEntity, Long.class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @Test
    public void 메뉴관리_트리_이름변경한다() throws Exception {
        Site site = siteRepository.findAll().get(0);

        Menu parentMenu1 = menuRepository.save(Menu.builder()
                .menuKorName("parent_1")
                .sortSeq(1)
                .site(site)
                .build());

        Long menuId = 0L;
        for (int i = 0; i < 3; i++) {
            Menu childMenu1 = Menu.builder()
                    .menuKorName("child_1_" + i)
                    .site(site)
                    .parent(Optional.of(parentMenu1))
                    .sortSeq(i + 1)
                    .build();
            childMenu1.setParentMenu(parentMenu1);
            Menu save = menuRepository.save(childMenu1);
            menuId = save.getId();
        }

        String url = "/api/v1/menus/"+menuId+"/updateName";

        //when
        ResponseEntity<MenuTreeResponseDto> responseEntity =
                restTemplate.exchange(url, HttpMethod.PUT, null, MenuTreeResponseDto.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().getName()).isEqualTo("updateName");

    }

    @Test
    public void 메뉴관리_기본설정_저장한다() throws Exception {
        //given
        Site site = siteRepository.findAll().get(0);

        Menu parentMenu1 = menuRepository.save(Menu.builder()
                .menuKorName("parent_1")
                .sortSeq(1)
                .site(site)
                .build());

        String url = "/api/v1/menus/"+parentMenu1.getId();


        HttpEntity<MenuUpdateRequestDto> httpEntity = new HttpEntity<>(
                MenuUpdateRequestDto.builder()
                        .description("상위메뉴")
                        .connectId(1)
                        .menuType("menuType")
                        .urlPath("/index")
                        .subName("subname")
                        .isUse(true)
                        .isShow(true)
                        .isBlank(false)
                        .icon("icon")
                .build()
        );

        //when
        ResponseEntity<MenuResponseDto> responseEntity =
                restTemplate.exchange(url, HttpMethod.PUT, httpEntity, MenuResponseDto.class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).extracting("description", "isUse").containsExactly("상위메뉴", true);
        System.out.println(responseEntity.getBody());

    }

    @Test
    public void 메뉴관리_한건_삭제한다() throws Exception {
        //given
        Site site = siteRepository.findAll().get(0);

        Menu parentMenu1 = menuRepository.save(Menu.builder()
                .menuKorName("parent_1")
                .sortSeq(1)
                .site(site)
                .build());

        Long menuId = 0L;
        for (int i = 0; i < 3; i++) {
            Menu childMenu1 = Menu.builder()
                    .menuKorName("child_1_" + i)
                    .site(site)
                    .parent(Optional.of(parentMenu1))
                    .sortSeq(i + 1)
                    .build();
            childMenu1.setParentMenu(parentMenu1);
            Menu save = menuRepository.save(childMenu1);
            menuId = save.getId();
        }

        String url = "/api/v1/menus/"+menuId;
        //when
        restTemplate.delete(url);

        //then
        Optional<Menu> optional = menuRepository.findById(menuId);
        assertThat(optional.isPresent()).isFalse();

    }

    @Test
    public void 메뉴관리_부모메뉴로_모두_삭제한다() throws Exception {
        //given
        Site site = siteRepository.findAll().get(0);

        Menu parentMenu1 = menuRepository.save(Menu.builder()
                .menuKorName("parent_1")
                .sortSeq(1)
                .site(site)
                .build());

        Long menuId = 0L;
        for (int i = 0; i < 3; i++) {
            Menu childMenu1 = Menu.builder()
                    .menuKorName("child_1_" + i)
                    .site(site)
                    .parent(Optional.of(parentMenu1))
                    .sortSeq(i + 1)
                    .build();
            childMenu1.setParentMenu(parentMenu1);
            Menu save = menuRepository.save(childMenu1);
            menuId = save.getId();
        }

        String url = "/api/v1/menus/"+parentMenu1.getId();
        //when
        restTemplate.delete(url);

        //then
        List<Menu> menus = menuRepository.findAll();
        assertThat(menus.size()).isEqualTo(0);

    }
}