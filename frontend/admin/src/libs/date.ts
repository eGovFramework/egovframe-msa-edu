import { TZ } from '@constants/env'
import { format as fnsFormat, Locale } from 'date-fns'
import { utcToZonedTime } from 'date-fns-tz'
import { ko, enUS } from 'date-fns/locale'

type DateType = number | Date

export const defaultlocales: Record<string, Locale> = { ko, enUS }

const locale =
  typeof window !== 'undefined'
    ? defaultlocales[window.__localeId__]
    : defaultlocales[global.__localeId__] // Check browser, server

// by providing a default string of 'PP' or any of its variants for `formatStr`
// it will format dates in whichever way is appropriate to the locale
export const format = (date: DateType, formatStr = 'PP') => {
  return fnsFormat(date, formatStr, {
    locale,
  })
}

export const getCurrentDate = (timezone?: string) => {
  return utcToZonedTime(Date.now(), timezone || TZ)
}

export const convertStringToDate = (
  date: string | Date,
  timezone: string = TZ,
) => {
  return utcToZonedTime(new Date(date), timezone)
}

export const convertStringToDateFormat = (
  date: string | Date,
  formatStr = 'yyyy-MM-dd',
  timezone: string = TZ,
) => {
  return format(convertStringToDate(date, timezone), formatStr)
}
