import { atomFamily, DefaultValue, selectorFamily } from 'recoil'
import { fieldIdsAtom } from './condition'

/**
 * page 상태관리
 * key를 기준으로 메뉴별로 사용
 */

export const pageAtom = atomFamily<number, string>({
  key: 'pageAtom',
  default: undefined,
})

export const pageSelector = selectorFamily<number, string>({
  key: 'pageSelector',
  get:
    id =>
    ({ get }) =>
      get(pageAtom(id)),
  set:
    id =>
    ({ set, get }, newValue) => {
      set(pageAtom(id), newValue)
      const ids = get(fieldIdsAtom)
      if (!ids.includes(id)) {
        set(fieldIdsAtom, prev => [...prev, id])
      }
    },
})

export const pageStateSelector = selectorFamily<
  Record<string, number>,
  string[]
>({
  key: 'pageStateSelector',
  get:
    ids =>
    ({ get }) => {
      return ids.reduce<Record<string, number>>((result, id) => {
        const value = get(pageAtom(id))
        return {
          ...result,
          [id]: value,
        }
      }, {})
    },
  set:
    ids =>
    ({ get, set, reset }, newValue) => {
      if (newValue instanceof DefaultValue) {
        reset(fieldIdsAtom)
        const ids = get(fieldIdsAtom)
        ids.forEach(id => reset(pageAtom(id)))
      } else {
        set(fieldIdsAtom, Object.keys(newValue))
        ids.forEach(id => {
          set(pageAtom(id), newValue[id])
        })
      }
    },
})
