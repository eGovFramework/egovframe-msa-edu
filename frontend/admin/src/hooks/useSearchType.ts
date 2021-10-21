import { IKeywordType } from '@components/Search'
import { useEffect, useState } from 'react'

export default function useSearchTypes(init: IKeywordType[]) {
  const [searchTypes, setSearchTypes] = useState<IKeywordType[]>([])

  useEffect(() => {
    setSearchTypes(init)
  }, [])

  return searchTypes
}
