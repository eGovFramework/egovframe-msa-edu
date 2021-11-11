import ActiveLink from '@components/ActiveLink'
import CustomSwiper from '@components/CustomSwiper'
import { LOAD_IMAGE_URL } from '@constants'
import { ASSET_PATH, SERVER_API_URL } from '@constants/env'
import { convertStringToDateFormat } from '@libs/date'
import { IBoard, IMainItem } from '@service'
import { userAtom } from '@stores'
import { useRouter } from 'next/router'
import { useSnackbar } from 'notistack'
import React, { useCallback, useEffect, useState } from 'react'
import { useRecoilValue } from 'recoil'
import { SwiperSlide } from 'swiper/react'
import { bannerTypeCodes, MainProps } from '.'

interface MainLGProps extends MainProps {
  reserveItems: IMainItem
}

const MainLG = (props: MainLGProps) => {
  const { banners, boards, reserveItems } = props
  const router = useRouter()

  const user = useRecoilValue(userAtom)
  const { enqueueSnackbar } = useSnackbar()

  const [activeNotice, setActiveNotice] = useState<number>(
    Number(Object.keys(boards)[0]),
  )
  const [activeBoard, setActiveBoard] = useState<number>(
    Number(Object.keys(boards)[2]),
  )
  const [notice, setNotice] = useState(undefined)
  const [board, setBoard] = useState(undefined)

  const [mainBanners, setMainBanners] = useState(undefined)
  const [items, setItems] = useState(undefined)
  const [activeItem, setAcitveItem] = useState<string>(
    Object.keys(reserveItems)[0],
  )

  //예약 물품
  useEffect(() => {
    if (reserveItems) {
      const active = reserveItems[activeItem]
      setItems(
        active?.map(reserveItem => (
          <SwiperSlide key={`reserve-item-${reserveItem.reserveItemId}`}>
            <h5>{reserveItem.categoryName}</h5>
            <dl>
              <dt>예약서비스</dt>
              <dd>{reserveItem.reserveItemName}</dd>
              <p>{`${convertStringToDateFormat(
                reserveItem.startDate,
                'yyyy-MM-dd',
              )} ~ ${convertStringToDateFormat(
                reserveItem.endDate,
                'yyyy-MM-dd',
              )}`}</p>
              <ActiveLink
                handleActiveLinkClick={() => {
                  if (!reserveItem.isPossible) {
                    return
                  }

                  if (user == null) {
                    enqueueSnackbar('로그인이 필요합니다.', {
                      variant: 'warning',
                    })
                    return
                  }

                  router.push(
                    `/reserve/${reserveItem.categoryId}/${reserveItem.reserveItemId}`,
                  )
                }}
                className={reserveItem.isPossible ? 'possible' : ''}
                href="#"
              >
                {reserveItem.isPossible ? '예약 가능' : '예약 불가'}
              </ActiveLink>
            </dl>
          </SwiperSlide>
        )),
      )
    }
  }, [reserveItems, activeItem])

  // 메인 배너
  useEffect(() => {
    if (banners) {
      setMainBanners(
        banners[bannerTypeCodes[0]]?.map((b, i) => {
          return (
            <SwiperSlide
              key={`main-banner-${b.bannerNo}`}
              style={{
                backgroundImage: `url(${SERVER_API_URL}${LOAD_IMAGE_URL}${b.uniqueId})`,
                backgroundRepeat: 'no-repeat',
                backgroundSize: 'cover',
                backgroundPosition: 'center',
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

  //boards
  useEffect(() => {
    if (boards) {
      const active = boards[activeBoard]
      setBoard(drawDivs(active))
    }
  }, [boards, activeBoard])

  useEffect(() => {
    if (boards) {
      const active = boards[activeNotice]
      setNotice(drawDivs(active))
    }
  }, [boards, activeNotice])

  //board 아이템 draw
  const drawDivs = useCallback(
    (board: IBoard) => {
      return (
        board && (
          <div key={`board-div-${board.boardNo}`}>
            {board.posts?.map(post => (
              <dl key={`posts-dl-${post.postsNo}`}>
                <dt>
                  <ActiveLink
                    href={`/board/${board.skinTypeCode}/${board.boardNo}/view/${post.postsNo}`}
                  >
                    {post.isNew ? <span className="newIcon">NEW</span> : null}
                    {post.postsTitle}
                  </ActiveLink>
                </dt>
                <dd>
                  <span>
                    {convertStringToDateFormat(post.createdDate, 'yyyy-MM-dd')}
                  </span>
                </dd>
              </dl>
            ))}
            <ActiveLink
              key={`board-more-${board.boardNo}`}
              href={`/board/${board.skinTypeCode}/${board.boardNo}`}
            >
              더보기
            </ActiveLink>
          </div>
        )
      )
    },
    [boards, activeBoard, activeNotice],
  )

  // 게시판 목록 draw
  const drawBoardList = () => {
    const boardNos = Object.keys(boards)

    let ul = []
    let children = []
    boardNos.map((no, idx) => {
      const title = React.createElement(
        'h4',
        {
          key: `notice-h4-${no}`,
          className:
            Number(no) === activeBoard || Number(no) === activeNotice
              ? 'on'
              : '',
          onClick: () => {
            handleBoardClick(Number(no))
          },
        },
        boards[no].boardName,
      )

      children.push(
        React.createElement(
          'li',
          { key: `notice-li-${no}` },
          <>
            {title}
            {Number(no) === activeBoard
              ? board
              : Number(no) === activeNotice
              ? notice
              : null}
          </>,
        ),
      )

      if ((idx + 1) % 2 === 0) {
        ul.push(
          React.createElement(
            'ul',
            {
              key: `notice-ul-${no}`,
            },
            children,
          ),
        )

        children = []
      }
    })

    return React.createElement('div', null, ul)
  }

  const handleBoardClick = (no: number) => {
    if (no > 2) {
      setActiveBoard(no)
    } else {
      setActiveNotice(no)
    }
  }

  const handleItemClick = (key: string) => {
    setAcitveItem(key)
  }

  return (
    <>
      <div className="slide">
        {mainBanners && (
          <CustomSwiper
            slidesPerView={1}
            spaceBetween={30}
            pagination={{
              clickable: true,
            }}
            centeredSlides
            loop
            autoplay={{
              delay: 5000,
              disableOnInteraction: false,
            }}
            className="slideBox"
          >
            {mainBanners}
          </CustomSwiper>
        )}

        <div className="reservBox">
          <ul>
            {reserveItems &&
              Object.keys(reserveItems).map(key => (
                <li
                  key={`reserve-items-li-${key}`}
                  className={`box ${activeItem === key ? 'on' : ''}`}
                >
                  <ActiveLink
                    href="#"
                    handleActiveLinkClick={() => {
                      handleItemClick(key)
                    }}
                  >
                    {key}
                  </ActiveLink>
                </li>
              ))}
            <div>
              {items && (
                <CustomSwiper
                  slidesPerView={1}
                  spaceBetween={70}
                  pagination={{
                    clickable: true,
                  }}
                  centeredSlides
                  loop
                  autoplay={{
                    delay: 5000,
                    disableOnInteraction: false,
                  }}
                  className="reserve"
                >
                  {items}
                </CustomSwiper>
              )}
            </div>
          </ul>
        </div>
      </div>

      <div className="guide">
        <h3>가이드&amp;다운로드</h3>
        <ul>
          <li>
            <dl>
              <dt>개발환경</dt>
              <dd>
                개발 효율성을 높이는
                <br />
                개발자 도구
              </dd>
            </dl>
            <div>
              <img
                src={`${ASSET_PATH}/images/main/main_icon01.png`}
                alt="개발환경"
              />
            </div>
            <div className="downIcon">
              <a href="#">가이드</a>
              <a href="#">다운로드</a>
            </div>
          </li>
          <li>
            <dl>
              <dt>실행환경</dt>
              <dd>
                업무 프로그램 개발 시<br />
                필요한 응용프로그램 환경
              </dd>
            </dl>
            <div>
              <img
                src={`${ASSET_PATH}/images/main/main_icon02.png`}
                alt="실행환경"
              />
            </div>
            <div className="downIcon">
              <a href="#">가이드</a>
              <a href="#">다운로드</a>
            </div>
          </li>
          <li>
            <dl>
              <dt>운영환경</dt>
              <dd>
                서비스를 운영하기 위한
                <br />
                환경구성 제공
              </dd>
            </dl>
            <div>
              <img
                src={`${ASSET_PATH}/images/main/main_icon03.png`}
                alt="운영환경"
              />
            </div>
            <div className="downIcon">
              <a href="#">가이드</a>
              <a href="#">다운로드</a>
            </div>
          </li>
          <li>
            <dl>
              <dt>공통컴포넌트</dt>
              <dd>
                재사용이 가능하도록 개발한
                <br /> 어플리케이션의 집합
              </dd>
            </dl>
            <div>
              <img
                src={`${ASSET_PATH}/images/main/main_icon04.png`}
                alt="공통컴포넌트"
              />
            </div>
            <div className="downIcon">
              <a href="#">가이드</a>
              <a href="#">다운로드</a>
            </div>
          </li>
        </ul>
      </div>
      <div className="notice">{drawBoardList()}</div>
      <div className="supportService">
        <div>
          <h3 className="blind">지원서비스</h3>
          <ul>
            <li>
              <dl>
                <dt>교육신청</dt>
                <dd>중소기업 개발자를 대상으로 활용교육 실시</dd>
              </dl>
              <a href="#">바로가기</a>
            </li>
            <li>
              <dl>
                <dt>호환성확인</dt>
                <dd>상용 솔루션 간에 연동 확인 서비스</dd>
              </dl>
              <a href="#">바로가기</a>
            </li>
            <li>
              <dl>
                <dt>적용점검</dt>
                <dd>기술 및 교육지원과 적용 점검 서비스</dd>
              </dl>
              <a href="#">바로가기</a>
            </li>
          </ul>
        </div>
        <div>
          <h3 className="blind">오픈소스 현황</h3>
          <div>
            <a href="#">
              버전별 오픈소스
              <br />
              SW 구성
            </a>
          </div>
        </div>
      </div>
    </>
  )
}

export { MainLG }
