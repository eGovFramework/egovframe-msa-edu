import ActiveLink from '@components/ActiveLink'
import { ASSET_PATH } from '@constants/env'
import Hidden from '@material-ui/core/Hidden'
import { ISideMenu, menuStateAtom } from '@stores'
import { translateToLang } from '@utils'
import React, { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useRecoilValue } from 'recoil'

const Footer = () => {
  const { i18n } = useTranslation()
  const menus = useRecoilValue(menuStateAtom)

  const [bottom, setBottom] = useState<ISideMenu>(undefined)

  useEffect(() => {
    if (menus) {
      setBottom(menus.find(item => item.menuType === 'bottom'))
    }
  }, [menus])

  return (
    <>
      <footer>
        <div>
          <ul>
            {bottom &&
              bottom.children.map(item => (
                <li key={`bottom-li-${item.id}`}>
                  <ActiveLink
                    href={item.urlPath}
                    children={translateToLang(i18n.language, item)}
                  />
                </li>
              ))}
          </ul>
          <Hidden xsDown>
            <div>
              <div>
                <dl>
                  <dt>대표문의메일</dt>
                  <dd>
                    <a href="mailto:egovframesupport@gmail.com">
                      egovframesupport@gmail.com
                    </a>
                  </dd>
                </dl>
                <dl>
                  <dt>대표전화</dt>
                  <dd>
                    <a href="tel:1566-3598">1566-3598(070-4448-2678)</a>
                  </dd>
                </dl>
              </div>
              <div>
                <dl>
                  <dt>호환성확인</dt>
                  <dd>
                    <a href="tel:070-4448-3673">070-4448-3673</a>
                  </dd>
                </dl>
                <dl>
                  <dt>교육문의</dt>
                  <dd>
                    <a href="tel:070-4448-2674">070-4448-2674</a>
                  </dd>
                </dl>
              </div>
              <p>
                Copyright (C) 2021 Ministry of the Interior and Safety.
                <br className="hidden" /> All Right Reserved.
              </p>
            </div>

            <span>
              <ActiveLink
                href="https://www.mois.go.kr"
                title="행정안전부 홈페이지로 이동합니다"
                children={
                  <img
                    src={`${ASSET_PATH}/images/layout/logo_mois.png`}
                    alt="행정안전부"
                  />
                }
              />
              <ActiveLink
                href="https://www.nia.or.kr"
                title="한국지능정보사회진흥원 홈페이지로 이동합니다"
                children={
                  <img
                    src={`${ASSET_PATH}/images/layout/logo_nia.png`}
                    alt="한국지능정보사회진흥원"
                  />
                }
              />
            </span>
          </Hidden>
          <Hidden smUp>
            <p className="mobCopy">
              (C) 표준프레임워크 포털 &nbsp;&nbsp; All Rights Reserved.
            </p>
          </Hidden>
        </div>
      </footer>
    </>
  )
}

export default Footer
