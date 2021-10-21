import { IMenuTree } from '@service'
import { atom } from 'recoil'

type ExpandedType = 'expand' | 'collapse' | 'none'

export const draggableTreeExpandedAtom = atom<ExpandedType>({
  key: 'draggableTreeExpandedAtom',
  default: 'none',
})

export const draggableTreeSelectedAtom = atom<IMenuTree | undefined>({
  key: 'draggableTreeSelectedAtom',
  default: undefined,
})

export const treeChangeNameAtom = atom<{
  state: 'change' | 'complete' | 'none'
  id?: number
  name?: string
}>({
  key: 'treeChangeNameAtom',
  default: {
    state: 'none',
  },
})
