import React, { useState } from 'react'

export default function useInputs(initial: ValueType) {
  const [value, setValue] = useState<ValueType>(initial)

  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setValue(event.target.value)
  }

  /**
   * @TODO
   * validation 추가필요
   */

  return {
    value,
    onChange: handleChange,
  }
}
