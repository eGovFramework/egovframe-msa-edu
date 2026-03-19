package org.egovframe.cloud.portalservice.api.menu;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.egovframe.cloud.portalservice.api.menu.dto.MenuRoleRequestDto;
import org.egovframe.cloud.portalservice.api.menu.dto.MenuRoleResponseDto;
import org.egovframe.cloud.portalservice.api.menu.dto.MenuSideResponseDto;
import org.egovframe.cloud.portalservice.domain.menu.Menu;
import org.egovframe.cloud.portalservice.domain.menu.MenuRepository;
import org.egovframe.cloud.portalservice.domain.menu.MenuRole;
import org.egovframe.cloud.portalservice.domain.menu.MenuRoleRepository;
import org.egovframe.cloud.portalservice.domain.menu.Site;
import org.egovframe.cloud.portalservice.domain.menu.SiteRepository;
import org.egovframe.cloud.portalservice.domain.user.Role;
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
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableConfigurationProperties
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@ActiveProfiles(profiles = "test")
class MenuRoleApiControllerTest {


    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private MenuRoleRepository menuRoleRepository;

    @BeforeEach
    public void setup() throws Exception {
        Site site = Site.builder()
                .name("site")
                .isUse(true)
                .build();
        siteRepository.save(site);

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
    }

    @AfterEach
    public void cleanup() throws Exception {
        menuRoleRepository.deleteAll();
        menuRepository.deleteAll();
        siteRepository.deleteAll();
    }

    @Test
    public void 권한별메뉴_데이터없이_메뉴outerjoin_하여_조회한다() throws Exception {
        Site site = siteRepository.findAll().get(0);
        //when
        ResponseEntity<List<MenuRoleResponseDto>> responseEntity =
                restTemplate.exchange("/api/v1/menu-roles/role/"+site.getId(), HttpMethod.GET, null, new ParameterizedTypeReference<List<MenuRoleResponseDto>>(){});

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<MenuRoleResponseDto> body = responseEntity.getBody();
        assertThat(body.size()).isEqualTo(1);
        assertThat(body.get(0).getChildren().size()).isEqualTo(3);
        body.stream().forEach(menuTreeResponseDto -> {
            System.out.println(menuTreeResponseDto.toString());
            menuTreeResponseDto.getChildren().stream().forEach(System.out::println);
        });
    }

    @Test
    public void 권한별메뉴_데이터있는경우_조회한다() throws Exception {

        List<Menu> menus = menuRepository.findAll();
        Menu parent = menus.stream().filter(menu -> menu.getMenuKorName().equals("parent")).collect(Collectors.toList()).get(0);
        Menu child1 = menus.stream().filter(menu -> menu.getMenuKorName().equals("child_1")).collect(Collectors.toList()).get(0);

        List<MenuRole> menuRoles = new ArrayList<>();
        MenuRole menuRole1 = MenuRole.builder().roleId("ROLE").menu(parent).build();
        menuRoles.add(menuRole1);
        MenuRole menuRole2 = MenuRole.builder().roleId("ROLE").menu(child1).build();
        menuRoles.add(menuRole2);

        menuRoleRepository.saveAll(menuRoles);

        Site site = siteRepository.findAll().get(0);
        //when
        ResponseEntity<List<MenuRoleResponseDto>> responseEntity =
                restTemplate.exchange("/api/v1/menu-roles/role/"+site.getId(), HttpMethod.GET, null, new ParameterizedTypeReference<List<MenuRoleResponseDto>>(){});


        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<MenuRoleResponseDto> body = responseEntity.getBody();
        assertThat(body.size()).isEqualTo(1);
        body.stream().forEach(System.out::println);
        assertThat(body.get(0).getIsChecked()).isTrue();
        assertThat(body.get(0).getChildren().size()).isEqualTo(3);
        body.stream().forEach(menuTreeResponseDto -> {
            menuTreeResponseDto.getChildren().stream().forEach(child -> {
                System.out.println(child);
                if (child.getKorName().equals("child_1")) {
                    assertThat(child.getIsChecked()).isTrue();
                }else {
                    assertThat(child.getIsChecked()).isFalse();
                }
            });

        });

    }

    @Test
    public void 권한별메뉴관리_저장한다() throws Exception {
        Site site = siteRepository.findAll().get(0);
        List<MenuRoleResponseDto> list = menuRoleRepository.findTree("role", site.getId());

        List<MenuRoleRequestDto> requestDtoList = new ArrayList<>();
        List<MenuRoleRequestDto> children = new ArrayList<>();
        list.get(0).getChildren().stream().forEach(menuRoleResponseDto -> {
            if (menuRoleResponseDto.getKorName().equals("child_1")) {
                children.add(MenuRoleRequestDto.builder()
                        .menuRoleId(menuRoleResponseDto.getMenuRoleId())
                        .isChecked(true)
                        .roleId("ROLE")
                        .id(menuRoleResponseDto.getId())
                        .build());

            }else {
                children.add(MenuRoleRequestDto.builder()
                        .menuRoleId(menuRoleResponseDto.getMenuRoleId())
                        .isChecked(false)
                        .roleId("ROLE")
                        .id(menuRoleResponseDto.getId())
                        .build());
            }
        });

        requestDtoList.add(MenuRoleRequestDto.builder()
                .menuRoleId(list.get(0).getMenuRoleId())
                .isChecked(true)
                .roleId("ROLE")
                .id(list.get(0).getId())
                .children(children)
                .build());

        HttpEntity<List<MenuRoleRequestDto>> httpEntity = new HttpEntity<>(
                requestDtoList
        );



        //when
        ResponseEntity<String> responseEntity =
                restTemplate.exchange("/api/v1/menu-roles", HttpMethod.POST, httpEntity, String.class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getBody()).isEqualTo("Success");

        List<MenuRole> roles = menuRoleRepository.findAll();
        roles.stream().forEach(System.out::println);
        assertThat(roles.size()).isEqualTo(2);

    }

    @Test
    public void 로그인하지_않은_사용자의_메뉴조회() throws Exception {
        //given
        Site site = siteRepository.findAll().get(0);
        Menu parentMenu = menuRepository.save(Menu.builder()
                .menuKorName("parent-any")
                .sortSeq(1)
                .site(site)
                .isUse(true)
                .build());
        MenuRole parentMenuRole = MenuRole.builder()
                .roleId(Role.ANONYMOUS.getKey())
                .menu(parentMenu)
                .build();
        parentMenuRole.setMenu(parentMenu);
        menuRoleRepository.save(parentMenuRole);

        for (int i = 0; i < 3; i++) {
            Menu childMenu = Menu.builder()
                    .menuKorName("child-any_" + i)
                    .site(site)
                    .parent(Optional.of(parentMenu))
                    .sortSeq(i + 1)
                    .isUse(true)
                    .build();
            childMenu.setParentMenu(parentMenu);
            menuRepository.save(childMenu);
            MenuRole role_any = MenuRole.builder()
                    .roleId(Role.ANONYMOUS.getKey())
                    .menu(childMenu)
                    .build();
            role_any.setMenu(childMenu);
            menuRoleRepository.save(role_any);
        }
        //when
        ResponseEntity<List<MenuSideResponseDto>> responseEntity =
                restTemplate.exchange("/api/v1/menu-roles/"+site.getId(), HttpMethod.GET, null, new ParameterizedTypeReference<List<MenuSideResponseDto>>(){});


        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<MenuSideResponseDto> body = responseEntity.getBody();
        assertThat(body.size()).isEqualTo(1);
        body.stream().forEach(menuSideResponseDto -> {
            System.out.println(menuSideResponseDto);
            menuSideResponseDto.getChildren().stream().forEach(System.out::println);
        });

    }
}