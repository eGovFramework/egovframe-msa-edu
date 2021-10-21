import { pageAtom, pageSelector } from '@stores'
import { useState } from 'react'
import { useRecoilValue, useSetRecoilState } from 'recoil'

export default function usePage(conditionKey: string, initPage: number = 0) {
  const pageState = useRecoilValue(pageAtom(conditionKey))
  const setValue = useSetRecoilState(pageSelector(conditionKey))

  const [page, setPage] = useState<number>(pageState || initPage)

  const setPageValue = (num: number) => {
    setValue(num)
    setPage(num)
  }

  return { page, setPageValue }
}
