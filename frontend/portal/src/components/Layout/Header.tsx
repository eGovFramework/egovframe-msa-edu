import ActiveLink from '@components/ActiveLink'
import Sitemap from '@components/Sitemap'
import { DEFAULT_APP_NAME } from '@constants'
import { ASSET_PATH } from '@constants/env'
import Hidden from '@material-ui/core/Hidden'
import { currentMenuStateAtom, menuStateAtom, userAtom } from '@stores'
import React, { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useRecoilValue } from 'recoil'

const Header = () => {
  const { t, i18n } = useTranslation()
  const menus = useRecoilValue(menuStateAtom)
  const currentMenu = useRecoilValue(currentMenuStateAtom)

  const user = useRecoilValue(userAtom)

  const [sitemapState, setSitemapState] = useState<boolean>(false)

  useEffect(() => {
    setSitemapState(false)
  }, [currentMenu])

  const handleSitemap = (event: React.MouseEvent<HTMLAnchorElement>) => {
    event.preventDefault()
    setSitemapState(!sitemapState)
  }

  return (
    <>
      <header>
        <div>
          <h1>
            <Hidden smUp>
              <ActiveLink
                href="/"
                children={
                  <img
                    src={`${ASSET_PATH}/images/layout/h1_logo_mob.png`}
                    alt={DEFAULT_APP_NAME}
                  />
                }
              />
            </Hidden>
            <Hidden xsDown>
              <ActiveLink
                href="/"
                children={
                  <img
                    src={`${ASSET_PATH}/images/layout/h1_logo.png`}
                    alt={DEFAULT_APP_NAME}
                  />
                }
              />
            </Hidden>
          </h1>
          <Hidden xsDown>
            <nav>
              <ul>
                {menus &&
                  menus
                    .filter(item => item.isShow)
                    .map(item => (
                      <li key={`li-header-${item.id}`}>
                        <ActiveLink
                          key={`header-${item.id}`}
                          href={
                            item.children.length > 0
                              ? item.children[0].urlPath
                              : item.urlPath
                          }
                          children={
                            i18n.language === 'ko' ? item.korName : item.engName
                          }
                        />
                      </li>
                    ))}
              </ul>
            </nav>
          </Hidden>

          <div className={`sitemap ${sitemapState ? 'on' : ''}`}>
            {user ? (
              <ActiveLink href="/auth/logout" children={t('common.logout')} />
            ) : (
              <>
                <ActiveLink
                  href="/auth/login"
                  className="login"
                  children={t('common.login')}
                />
                <ActiveLink href="/auth/join" children={t('common.join')} />
              </>
            )}

            <div>
              <a href="#" className="btn" onClick={handleSitemap}>
                {t('common.sitemap')}
              </a>
              {sitemapState && <Sitemap />}
            </div>
          </div>
        </div>
      </header>
    </>
  )
}

export default Header
