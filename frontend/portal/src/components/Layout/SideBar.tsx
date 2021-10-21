import React from 'react'
import { useRecoilValue } from 'recoil'
import { currentMenuStateAtom, sideMenuSelect } from '@stores'
import ActiveLink from '@components/ActiveLink'
import { useTranslation } from 'react-i18next'
import { translateToLang } from '@utils'

const SideBar = () => {
  const currentMenu = useRecoilValue(currentMenuStateAtom)
  const menus = useRecoilValue(sideMenuSelect)
  const { i18n } = useTranslation()

  return (
    <>
      {menus && (
        <nav>
          <ul>
            {menus.map(item => (
              <li
                key={`sidebar-li-${item.id}`}
                className={`${item.id === currentMenu.id ? 'on' : null}`}
              >
                <ActiveLink
                  key={`sidebar-li-a-${item.id}`}
                  href={item.urlPath}
                  children={translateToLang(i18n.language, item)}
                />
              </li>
            ))}
          </ul>
        </nav>
      )}
    </>
  )
}

export default SideBar
