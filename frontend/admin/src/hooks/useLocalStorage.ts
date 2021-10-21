import { useState } from 'react'

export const useLocalStorage = (key: string, initialValue: unknown = '') => {
  const [storeValue, setStoreValue] = useState(() => {
    try {
      const item = window.localStorage.getItem(key)
      return item ? JSON.parse(item) : initialValue
    } catch (error) {
      return initialValue
    }
  })

  const setValue = (value: unknown) => {
    try {
      const valueToStore = value instanceof Function ? value(storeValue) : value

      setStoreValue(valueToStore)
      window.localStorage.setItem(key, JSON.stringify(valueToStore))
    } catch (error) {
      console.error(`useLocalStorage setValue error : ${error.message}`)
    }
  }

  return [storeValue, setValue]
}
