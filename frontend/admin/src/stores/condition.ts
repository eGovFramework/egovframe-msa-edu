import { atom, atomFamily, DefaultValue, selectorFamily } from 'recoil'

/**
 * 조회조건 상태관리
 * key를 기준으로 메뉴별로 사용
 */

export type conditionValue = { [key: string]: string }

export const conditionAtom = atomFamily<conditionValue, string>({
  key: 'conditionAtom',
  default: undefined,
})

export const fieldIdsAtom = atom<string[]>({
  key: 'fieldIdsAtom',
  default: [],
})

export const conditionSelector = selectorFamily<conditionValue, string>({
  key: 'conditionSelector',
  get:
    id =>
    ({ get }) =>
      get(conditionAtom(id)),
  set:
    id =>
    ({ set, get }, newValue) => {
      set(conditionAtom(id), newValue)
      const ids = get(fieldIdsAtom)
      if (!ids.includes(id)) {
        set(fieldIdsAtom, prev => [...prev, id])
      }
    },
})

export const conditionStateSelector = selectorFamily<
  Record<string, conditionValue>,
  string[]
>({
  key: 'conditionStateSelector',
  get:
    ids =>
    ({ get }) => {
      return ids.reduce<Record<string, conditionValue>>((result, id) => {
        const value = get(conditionAtom(id))
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
        ids.forEach(id => reset(conditionAtom(id)))
      } else {
        set(fieldIdsAtom, Object.keys(newValue))
        ids.forEach(id => {
          set(conditionAtom(id), newValue[id])
        })
      }
    },
})
