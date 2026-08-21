import { COMMENTS_MAX_LENGTH } from '@constants'
import { getTextLength, truncateText } from '@utils'
import React, { useState } from 'react'

export interface ITextarea {
  value: ValueType | ReadonlyArray<string>
  currentCount: number
}

export default function useTextarea(
  initial: ITextarea,
  maxLength: number = COMMENTS_MAX_LENGTH,
) {
  const [textarea, setTextarea] = useState<ITextarea>(initial)
  const max = maxLength

  const handleChange = (event: React.ChangeEvent<HTMLTextAreaElement>) => {
    const targetValue = event.target.value
    const count = getTextLength(targetValue)
    if (count > max) {
      const truncatedValue = truncateText(targetValue, max)
      setTextarea({
        value: truncatedValue,
        currentCount: getTextLength(truncatedValue),
      })

      return
    }

    setTextarea({
      value: targetValue,
      currentCount: count,
    })
  }

  const clearTextarea = () => {
    setTextarea({
      value: '',
      currentCount: 0,
    })
  }

  /**
   * @TODO
   * validation 추가필요
   */

  return {
    currentCount: textarea.currentCount,
    clear: clearTextarea,
    value: textarea.value,
    onChange: handleChange,
  }
}
