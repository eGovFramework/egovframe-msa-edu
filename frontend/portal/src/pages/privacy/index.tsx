import { IPrivacy, privacyService } from '@service'
import { GetServerSideProps } from 'next'
import React, { useRef, useState } from 'react'
import { useTranslation } from 'react-i18next'

export interface IPrivacyItemsProps {
  privacies: IPrivacy[] | null
}

const Privacy = ({ privacies }: IPrivacyItemsProps) => {
  const { t } = useTranslation()

  const [privacyIndex, setPrivacyIndex] = useState<number>(0)
  const privacySelectRef = useRef<any>(null)

  const handleSearch = () => {
    setPrivacyIndex(privacySelectRef.current?.selectedIndex)
  }

  return (
    <>
      <article className="rocation">
        <h2>{t('privacy')}</h2>
      </article>
      <article className="privacy">
        <fieldset>
          <select title={t('common.select')} ref={privacySelectRef}>
            {privacies &&
              privacies.map((p, i) => (
                <option key={`privacy-${p.privacyNo}`} value={i}>
                  {p.privacyTitle}
                </option>
              ))}
          </select>
          <button onClick={handleSearch}>{t('common.search')}</button>
        </fieldset>
        <div
          dangerouslySetInnerHTML={{
            __html: privacies[privacyIndex]?.privacyContent,
          }}
        />
      </article>
    </>
  )
}

export const getServerSideProps: GetServerSideProps = async () => {
  let privacies = {}

  try {
    const result = await privacyService.alluse()
    if (result) {
      privacies = (await result.data) as IPrivacy[]
    }
  } catch (error) {
    console.error(`privacy item query error ${error.message}`)
    if (error.response?.data?.code === 'E003') {
      return {
        notFound: true,
      }
    }
  }

  return {
    props: {
      privacies,
    },
  }
}

export default Privacy
