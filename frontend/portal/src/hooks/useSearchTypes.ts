import { useEffect, useState } from 'react'
import { OptionsType } from '@components/Inputs'

export default function useSearchTypes(init: OptionsType[]) {
  const [searchTypes, setSearchTypes] = useState<OptionsType[]>([])

  useEffect(() => {
    setSearchTypes(init)
  }, [])

  return searchTypes
}
