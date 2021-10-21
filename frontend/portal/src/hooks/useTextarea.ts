import { COMMENTS_MAX_LENGTH } from '@constants'
import { getTextLength } from '@utils'
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
    let targetValue = event.target.value
    const count = getTextLength(targetValue)
    if (count > max) {
      setTextarea({
        value: targetValue.slice(0, max),
        currentCount: max,
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
