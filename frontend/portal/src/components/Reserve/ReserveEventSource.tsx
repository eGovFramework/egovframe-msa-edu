import { SERVER_API_URL } from '@constants/env'
import { IReserve, reserveService } from '@service'
import React, { useEffect, useState } from 'react'

interface ReserveEventSourceProps {
  data: IReserve
  successCallback: () => void
  errorCallback: (error: any, attachmentCode: string) => void
}

const ReserveEventSource = ({
  data,
  successCallback,
  errorCallback,
}: ReserveEventSourceProps) => {
  const [isSuccess, setSuccess] = useState<string>(null)

  useEffect(() => {
    let eventSource: EventSource = null
    if (data) {
      eventSource = new EventSource(
        `${SERVER_API_URL}${reserveService.requestApiUrl}/direct/${data.reserveId}`,
      )

      eventSource.onmessage = event => {
        if (event.data !== 'no news is good news') {
          setSuccess(event.data)
          eventSource.close()
        }
      }
      eventSource.onerror = err => {
        console.error('EventSource failed:', err)
        eventSource.close()
      }
    }

    return () => {
      if (eventSource) {
        eventSource.close()
        eventSource = null
      }
    }
  }, [data])

  useEffect(() => {
    if (isSuccess) {
      if (isSuccess === 'true') {
        successCallback()
      } else {
        errorCallback({ message: '예약에 실패했습니다.' }, data.attachmentCode)
      }
    }
  }, [isSuccess])

  return <>{isSuccess}</>
}

export default ReserveEventSource
