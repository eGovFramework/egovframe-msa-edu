import React, { useState } from 'react'

type Props = {
  initialLoginStatus: string
}

function Reload(props: Props) {
  const [reloadState, setReloadSteate] = useState<{
    message: string
    severity: 'success' | 'info' | 'error' | 'warning'
  }>({
    message: 'reload message!!',
    severity: 'info',
  })

  const onClickReload = async (event: React.MouseEvent<HTMLButtonElement>) => {
    event.preventDefault()
    fetch('/api/v1/messages')
      .then(async response => {
        const result = await response.json()
        if (response.ok) {
          setReloadSteate({
            message: result.message,
            severity: 'success',
          })
        } else {
          setReloadSteate({
            message: result.message,
            severity: 'error',
          })
        }
      })
      .catch(error => {
        setReloadSteate({
          message: error.message,
          severity: 'error',
        })
      })
  }

  return (
    <div>
      <span>{reloadState.message}</span>
      <button onClick={onClickReload}>Reload</button>
    </div>
  )
}

export default Reload
