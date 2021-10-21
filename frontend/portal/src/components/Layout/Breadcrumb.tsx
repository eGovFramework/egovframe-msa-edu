import { currentMenuStateAtom, flatMenusSelect, ISideMenu } from '@stores'
import { translateToLang } from '@utils'
import React, { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useRecoilValue } from 'recoil'

interface IBreadcrumb {
  id: ValueType
  name: string
}

const Breadcrumb = () => {
  const currentMenu = useRecoilValue(currentMenuStateAtom)
  const flatMenus = useRecoilValue(flatMenusSelect)

  const { t, i18n } = useTranslation()

  const [breadState, setBreadState] = useState<IBreadcrumb[]>(undefined)

  useEffect(() => {
    if (currentMenu) {
      const nodes: IBreadcrumb[] = []
      const arr = flatMenus.slice(
        0,
        flatMenus.findIndex(item => item.id === currentMenu.id) + 1,
      )

      nodes.push({
        id: currentMenu.id,
        name: translateToLang(i18n.language, currentMenu),
      })

      arr.reverse().some((item: ISideMenu) => {
        if (item.level < currentMenu.level) {
          nodes.push({
            id: item.id,
            name: translateToLang(i18n.language, item),
          })
        }

        if (item.level === 1) {
          return true
        }
      })
      nodes.push({
        id: 'home',
        name: '홈',
      })

      const bread: IBreadcrumb[] = nodes.reverse().slice()

      setBreadState(bread)
    }
  }, [currentMenu])

  return (
    <>
      <ul>
        {breadState &&
          breadState.map(item => (
            <li key={`bread-li-${item.id}`}>{item.name}</li>
          ))}
      </ul>
    </>
  )
}

export default Breadcrumb
