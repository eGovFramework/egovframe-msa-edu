import i18n from 'i18next'
import { initReactI18next } from 'react-i18next'

import translationEn from 'public/locales/en/common.json'
import translationKo from 'public/locales/ko/common.json'
import { DEV } from '@constants/env'

const resources = {
  en: {
    translation: translationEn,
  },
  ko: {
    translation: translationKo,
  },
}

i18n.use(initReactI18next).init({
  resources,
  lng: 'ko',
  fallbackLng: 'ko',
  debug: DEV,
  keySeparator: false,
  interpolation: {
    escapeValue: false,
  },
})

export default i18n
