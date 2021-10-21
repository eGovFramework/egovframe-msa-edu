import { atom, selector } from 'recoil'

/**
 * 사이드 메뉴 상태관리
 */
export interface ISideMenu {
  children: ISideMenu[]
  engName: string
  icon: string
  id: number
  isChecked: boolean
  korName: string
  level: number
  menuRoleId: number
  parentId: number
  roleId: string
  sortSeq: number
  urlPath: string
  expanded: boolean
  isShow: boolean
}

export const menuStateAtom = atom({
  key: 'menuStateAtom',
  default: [] as ISideMenu[],
})

export const currentMenuStateAtom = atom({
  key: 'currentMenuStateAtom',
  default: {} as ISideMenu,
})

export const flatMenusSelect = selector({
  key: 'flatMenusSelect',
  get: ({ get }) => {
    const menus = get(menuStateAtom)

    let flatMenus = []
    const getAllItems = (menu: ISideMenu) => {
      flatMenus.push(menu)
      if (menu.children) {
        return menu.children.map(i => getAllItems(i))
      }
    }

    menus.forEach(item => {
      getAllItems(item)
    })

    return flatMenus
  },
})
