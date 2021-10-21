import { atom } from 'recoil'

export const detailButtonsSnackAtom = atom<'none' | 'success' | 'loading'>({
  key: 'detailButtonsSnackAtom',
  default: 'none',
})
