import { OptionsType, SelectBox, SelectType } from '@components/Inputs'
import useInputs from '@hooks/useInputs'
import { conditionAtom, conditionSelector, conditionValue } from '@stores'
import React, { createRef, useCallback, useMemo } from 'react'
import { useTranslation } from 'react-i18next'
import { useRecoilValue, useSetRecoilState } from 'recoil'

interface SearchProps {
  options?: OptionsType[]
  buttonTitle?: string
  handleSearch: () => void
  conditionKey: string // 조회조건 상태값을 관리할 키 값 (e.g. 이용약관관리 -> policy)
  customKeyword?: conditionValue
  conditionNodes?: React.ReactNode
  className?: string
}

const Search = (props: SearchProps) => {
  const {
    options,
    buttonTitle,
    handleSearch,
    conditionKey,
    customKeyword,
    conditionNodes,
    className,
  } = props
  const { t, i18n } = useTranslation()

  const setValue = useSetRecoilState(conditionSelector(conditionKey))
  const conditionState = useRecoilValue(conditionAtom(conditionKey))

  const searchText = useInputs(conditionState?.keyword || '')
  const conditionRef = createRef<SelectType>()

  const defaultOptions = useMemo(() => {
    return (
      options || [
        {
          value: 'title',
          label: t('posts.posts_title'),
        },
        {
          value: 'content',
          label: t('posts.posts_content'),
        },
      ]
    )
  }, [options, i18n])

  const search = useCallback(() => {
    setValue({
      keywordType: conditionRef.current?.selectedValue,
      keyword: searchText.value,
      ...customKeyword,
    })
    handleSearch()
  }, [conditionRef, searchText, customKeyword])

  const handleClick = (e: React.MouseEvent<HTMLButtonElement>) => {
    e.preventDefault()
    search()
  }

  const handleKeyPress = (event: React.KeyboardEvent<HTMLElement>) => {
    if (event.key === 'Enter') {
      event.preventDefault()
      search()
    }
  }

  const handleKeyUp = (event: React.KeyboardEvent<HTMLElement>) => {
    /* setValue({
      keywordType: conditionRef.current?.selectedValue,
      keyword: searchText.value,
      customKeyword,
    }) */
  }

  return (
    <>
      {conditionNodes}
      <SelectBox
        ref={conditionRef}
        options={defaultOptions}
        className={className}
      />
      <input
        type="text"
        title={`${t('common.search_word')}`}
        placeholder={`${t('common.search_word')}${t('msg.placeholder')}`}
        onKeyPress={handleKeyPress}
        onKeyUp={handleKeyUp}
        {...searchText}
      />
      <button onClick={handleClick}>
        {buttonTitle || t('label.button.find')}
      </button>
    </>
  )
}

export default Search
