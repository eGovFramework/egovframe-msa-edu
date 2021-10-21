import ActiveLink from '@components/ActiveLink'
import Hidden from '@material-ui/core/Hidden'
import IconButton from '@material-ui/core/IconButton'
import KeyboardArrowDownIcon from '@material-ui/icons/KeyboardArrowDown'
import KeyboardArrowUpIcon from '@material-ui/icons/KeyboardArrowUp'
import { currentMenuStateAtom, ISideMenu, menuStateAtom } from '@stores'
import { translateToLang } from '@utils'
import React, { useCallback, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useRecoilValue } from 'recoil'

const renderSitemap = (
  item: ISideMenu,
  locale: string = 'ko',
  current?: ISideMenu,
) => {
  if (item.children.length <= 0) {
    return
  }

  const children = item.children.map(child => {
    return React.createElement(
      'li',
      {
        key: `sitemap-li-${child.id}`,
        className: `${current?.id === child.id ? 'on' : ''}`,
      },
      <ActiveLink
        href={child.urlPath}
        children={translateToLang(locale, child)}
        key={`sitemap-a-${child.id}`}
      />,
    )
  })

  return React.createElement(
    'ul',
    { key: `sitemap-sub-ul-${item.id}` },
    children,
  )
}

const Sitemap = () => {
  const menus = useRecoilValue(menuStateAtom)
  const currentMenu = useRecoilValue(currentMenuStateAtom)
  const { i18n } = useTranslation()

  const [collapseItem, setCollapseItem] = useState<ValueType>(
    currentMenu ? currentMenu.parentId : menus[0].id,
  )

  const isActive = useCallback(
    (id: ValueType): boolean => {
      if (collapseItem) {
        if (collapseItem === id) {
          return true
        }

        return false
      }

      return false
    },
    [currentMenu, collapseItem],
  )

  const handleChevronClick = (
    e: React.MouseEvent<HTMLButtonElement>,
    item: ISideMenu,
    isActive: boolean,
  ) => {
    e.preventDefault()

    if (isActive) {
      setCollapseItem(undefined)
    } else {
      setCollapseItem(item.id)
    }
  }

  return (
    <>
      <nav>
        <div>
          {menus &&
            menus
              .filter(item => item.isShow)
              .map((item, index) => {
                return (
                  <ul key={`sitemap-ul-${item.id}`}>
                    <li
                      key={`sitemap-li-${item.id}`}
                      className={`${isActive(item.id) ? 'on' : ''}`}
                    >
                      <Hidden smUp>
                        <div>
                          <ActiveLink
                            href={
                              item.children.length > 0
                                ? item.children[0].urlPath
                                : item.urlPath
                            }
                            children={translateToLang(i18n.language, item)}
                          />

                          <IconButton
                            color="inherit"
                            onClick={e =>
                              handleChevronClick(e, item, isActive(item.id))
                            }
                          >
                            {isActive(item.id) ? (
                              <KeyboardArrowUpIcon />
                            ) : (
                              <KeyboardArrowDownIcon />
                            )}
                          </IconButton>
                        </div>

                        {collapseItem === item.id
                          ? renderSitemap(item, i18n.language, currentMenu)
                          : null}
                      </Hidden>
                      <Hidden xsDown>
                        {renderSitemap(item, i18n.language, currentMenu)}
                      </Hidden>
                    </li>
                  </ul>
                )
              })}
        </div>
      </nav>
    </>
  )
}

export default Sitemap
