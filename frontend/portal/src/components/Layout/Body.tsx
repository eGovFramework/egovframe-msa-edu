import { Hidden } from '@material-ui/core'
import { currentMenuStateAtom, flatMenusSelect } from '@stores'
import { translateToLang } from '@utils'
import React, { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useRecoilValue } from 'recoil'
import { LayoutProps } from '.'
import Breadcrumb from './Breadcrumb'
import SideBar from './SideBar'

interface BodyProps extends LayoutProps {}

const Body = (props: BodyProps) => {
  const { children } = props

  const { i18n } = useTranslation()

  const currentMenu = useRecoilValue(currentMenuStateAtom)
  const flatMenus = useRecoilValue(flatMenusSelect)
  const [titleState, setTitleState] =
    useState<{
      parent: string | undefined
      current: string | undefined
    }>(undefined)

  useEffect(() => {
    if (currentMenu) {
      const parent = flatMenus.find(item => item.id === currentMenu.parentId)
      if (!parent) {
        setTitleState({
          parent: translateToLang(i18n.language, currentMenu),
          current: undefined,
        })

        return
      }

      setTitleState({
        parent: translateToLang(i18n.language, parent),
        current: translateToLang(i18n.language, currentMenu),
      })
    }
  }, [currentMenu])

  return (
    <div id="container">
      <div>
        <Hidden smDown>
          <SideBar />
        </Hidden>
        <section>
          <article className="rocation">
            {titleState && <h2>{titleState.parent}</h2>}

            <Breadcrumb />
          </article>
          <article>
            {titleState && titleState.current && <h3>{titleState.current}</h3>}
            {children}
          </article>
        </section>
      </div>
    </div>
  )
}

export default Body
