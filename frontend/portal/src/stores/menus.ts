import { atom, selector } from 'recoil'

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
  menuType: string
}

export const menuStateAtom = atom({
  key: 'menuStateAtom',
  default: [] as ISideMenu[],
})

export const currentMenuStateAtom = atom({
  key: 'currentMenuStateAtom',
  default: {} as ISideMenu,
})

export const flatMenusSelect = selector<ISideMenu[]>({
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

export const sideMenuSelect = selector<ISideMenu[]>({
  key: 'sideMenuSelect',
  get: ({ get }) => {
    const current = get(currentMenuStateAtom)
    const flatMenus = get(flatMenusSelect)
    if (!current.parentId) {
      return undefined
    }

    const parent = flatMenus.find(item => item.id === current.parentId)
    return parent.children
  },
})
