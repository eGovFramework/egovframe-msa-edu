import ActiveLink from '@components/ActiveLink'
import CustomSwiper from '@components/CustomSwiper'
import { LOAD_IMAGE_URL } from '@constants'
import { ASSET_PATH, SERVER_API_URL } from '@constants/env'
import { format as dateFormat } from '@libs/date'
import { escapeHtmlNl } from '@utils'
import { useRouter } from 'next/router'
import React, { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { SwiperSlide } from 'swiper/react'
import { bannerTypeCodes, MainProps } from '.'

const MainSM = ({ banners, boards }: MainProps) => {
  const { t } = useTranslation()
  const router = useRouter()

  const [activeBoard, setActiveBoard] = useState<string>(Object.keys(boards)[0])
  const [mainBanners, setMainBanners] = useState(undefined)
  const [board, setBoard] = useState(undefined)

  // 메인 배너
  useEffect(() => {
    if (banners) {
      setMainBanners(
        banners[bannerTypeCodes[0]]?.map((b, i) => {
          return (
            <SwiperSlide
              key={`main-banner-${b.bannerNo}`}
              className={`slide`}
              style={{
                backgroundImage: `url(${SERVER_API_URL}${LOAD_IMAGE_URL}${b.uniqueId})`,
                backgroundRepeat: 'no-repeat',
                backgroundSize: '100% 100%',
              }}
            >
              <div className="slide-title">
                <p>{b.bannerTitle}</p>
              </div>
              <div className="slide-content">
                <p>{b.bannerContent}</p>
              </div>
              <a
                href={b.urlAddr}
                target={b.newWindowAt ? '_blank' : '_self'}
                rel="noreferrer"
              >
                더보기
              </a>
            </SwiperSlide>
          )
        }),
      )
    }
  }, [banners])

  //board
  useEffect(() => {
    if (boards) {
      setBoard(
        boards[activeBoard]?.posts?.map(post => {
          return (
            <dl key={post.postsNo}>
              <dt
                onClick={() => {
                  handlePostView(
                    boards[activeBoard]?.skinTypeCode,
                    post.boardNo,
                    post.postsNo,
                  )
                }}
              >
                {post.postsTitle}
              </dt>
              <dd>
                <p>{escapeHtmlNl(post.postsContent)}</p>
                <span>
                  {dateFormat(
                    new Date(post.createdDate as string),
                    'yyyy-MM-dd',
                  )}
                </span>
                <ActiveLink
                  href="#"
                  handleActiveLinkClick={() => {
                    handlePostView(
                      boards[activeBoard].skinTypeCode,
                      post.boardNo,
                      post.postsNo,
                    )
                  }}
                >
                  {t('posts.see_more')}
                </ActiveLink>
              </dd>
            </dl>
          )
        }),
      )
    }
  }, [boards, activeBoard])

  // 게시판 탭 포커스
  const handleBoardFocus = boardNo => {
    setActiveBoard(boardNo)
  }

  // 게시물 더보기
  const handlePostView = (skinTypeCode, boardNo, postsNo) => {
    router.push(`/board/${skinTypeCode}/${boardNo}/view/${postsNo}`)
  }

  return (
    <>
      {mainBanners && (
        <CustomSwiper
          slidesPerView={1}
          spaceBetween={30}
          pagination={{
            clickable: true,
          }}
          centeredSlides
          loop
          breakpoints={{
            641: {
              slidesPerView: 3,
            },
          }}
          autoplay={{
            delay: 5000,
            disableOnInteraction: false,
          }}
        >
          {mainBanners}
        </CustomSwiper>
      )}

      {Object.keys(boards).length > 0 && (
        <div className="notice">
          <ul>
            {Object.keys(boards).map(boardNo => {
              return !boards[boardNo] ? null : (
                <li key={`notice-li-${boardNo}`}>
                  <h4 className={`${activeBoard === boardNo ? 'on' : ''}`}>
                    <a
                      href="#"
                      onFocus={() => {
                        handleBoardFocus(boardNo)
                      }}
                      onMouseOver={() => {
                        handleBoardFocus(boardNo)
                      }}
                      onClick={event => {
                        event.preventDefault()
                        handleBoardFocus(boardNo)
                      }}
                    >
                      {boards[boardNo].boardName}
                    </a>
                  </h4>
                  {board && <div>{board}</div>}
                </li>
              )
            })}
          </ul>
        </div>
      )}
      <div className="guide">
        <h3>가이드&amp;다운로드</h3>
        <ul>
          <li>
            <figure>
              <img
                src={`${ASSET_PATH}/images/main/main_icon01.png`}
                alt="개발환경"
              />
              <figcaption>개발환경</figcaption>
            </figure>
            <p>
              개발 효율성을 높이는
              <br /> 개발자 도구
            </p>
            <div>
              <a href="#">가이드</a>
              <a href="#">다운로드</a>
            </div>
          </li>
          <li>
            <figure>
              <img
                src={`${ASSET_PATH}/images/main/main_icon02.png`}
                alt="실행환경"
              />
              <figcaption>실행환경</figcaption>
            </figure>
            <p>
              업무 프로그램 개발 시<br /> 필요한 응용프로그램 환경{' '}
            </p>
            <div>
              <a href="#">가이드</a>
              <a href="#">다운로드</a>
            </div>
          </li>
          <li>
            <figure>
              <img
                src={`${ASSET_PATH}/images/main/main_icon03.png`}
                alt="운영환경"
              />
              <figcaption>운영환경</figcaption>
            </figure>
            <p>
              서비스를 운영하기 위한
              <br /> 환경구성 제공
            </p>
            <div>
              <a href="#">가이드</a>
              <a href="#">다운로드</a>
            </div>
          </li>
          <li>
            <figure>
              <img
                src={`${ASSET_PATH}/images/main/main_icon04.png`}
                alt="공통컴포넌트"
              />
              <figcaption>공통컴포넌트</figcaption>
            </figure>
            <p>
              재사용이 가능하도록 개발한
              <br /> 어플리케이션의 집합
            </p>
            <div>
              <a href="#">가이드</a>
              <a href="#">다운로드</a>
            </div>
          </li>
        </ul>
        <div>
          <div>
            <h3>지원서비스</h3>
            <ul>
              <li>
                <dl>
                  <dt>교육신청</dt>
                  <dd>
                    중소기업 개발자를 대상으로
                    <br />
                    활용교육 실시
                  </dd>
                </dl>
                <div className="small">
                  <a href="#">가이드</a>
                  <a href="#">다운로드</a>
                </div>
              </li>
              <li>
                <dl>
                  <dt>호환성확인</dt>
                  <dd>
                    상용 솔루션 간에
                    <br />
                    연동 확인 서비스
                  </dd>
                </dl>
                <div className="small">
                  <a href="#">가이드</a>
                  <a href="#">다운로드</a>
                </div>
              </li>
              <li>
                <dl>
                  <dt>적용점검</dt>
                  <dd>
                    기술 및 교육지원과
                    <br />
                    적용 점검 서비스
                  </dd>
                </dl>
                <div className="small">
                  <a href="#">가이드</a>
                  <a href="#">다운로드</a>
                </div>
              </li>
            </ul>
          </div>
          <div>
            <h3>오픈소스 현황</h3>
            <div>
              <a href="#">
                버전별 오픈소스 <br />
                SW 구성
              </a>
            </div>
          </div>
        </div>
      </div>
    </>
  )
}

export { MainSM }
