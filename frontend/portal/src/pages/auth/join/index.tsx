import CustomAlert, { CustomAlertPrpps } from '@components/CustomAlert'
import { IPolicy, policyService } from '@service'
import { GetServerSideProps } from 'next'
import { useRouter } from 'next/router'
import React, { createRef, useState } from 'react'
import { useTranslation } from 'react-i18next'

export interface IJoinProps {
  policyTOS?: IPolicy
  policyPP?: IPolicy
}

const Join = ({ policyPP, policyTOS }: IJoinProps) => {
  const router = useRouter()
  const { t } = useTranslation()

  const agree1Ref = createRef<HTMLInputElement>()
  const agree2Ref = createRef<HTMLInputElement>()

  const [customAlert, setCustomAlert] = useState<any>({
    open: false,
    message: '',
    handleAlert: () => {
      setCustomAlert({ open: false })
    },
  } as CustomAlertPrpps)

  const handleCancel = event => {
    router.back()
  }

  // eslint-disable-next-line consistent-return
  const handleNext = (
    event: React.MouseEvent<HTMLAnchorElement, MouseEvent>,
  ) => {
    event.preventDefault()

    const agree1 = agree1Ref.current
    if (agree1?.checked !== true) {
      setCustomAlert({
        open: true,
        message: t('msg.join.agree1'),
        handleAlert: () => {
          setCustomAlert({ open: false })
          agree1?.focus() // visibility: hidden; 스타일 속성으로 인해 포커스 되지 않음
        },
      })
      return
    }

    const agree2 = agree1Ref.current
    if (agree2Ref.current?.checked !== true) {
      setCustomAlert({
        open: true,
        message: t('msg.join.agree2'),
        handleAlert: () => {
          setCustomAlert({ open: false })
          agree2?.focus()
        },
      })
      return
    }

    router.push(`/auth/join/form?provider=${router.query.provider}&token=${router.query.token}`)
  }

  return (
    <>
      <section className="member">
        <article className="rocation">
          <h2>{t('label.title.join')}</h2>
        </article>

        <article>
          <h3>{t('label.title.agree1')}</h3>
          <div className="join01">
            <div>
              <p dangerouslySetInnerHTML={{ __html: policyTOS.contents }} />
            </div>
            <div className="check">
              <input ref={agree1Ref} type="radio" id="termsOK" name="terms" />
              <label htmlFor="termsOK">{t('common.agree.y')}</label>
              <input type="radio" id="termsNO" name="terms" />
              <label htmlFor="termsNO">{t('common.agree.n')}</label>
            </div>
          </div>
          <h3>{t('label.title.agree2')}</h3>
          <div className="join01">
            <div>
              <p dangerouslySetInnerHTML={{ __html: policyPP.contents }} />
            </div>
            <div className="check">
              <input ref={agree2Ref} type="radio" id="infoOK" name="info" />
              <label htmlFor="infoOK">{t('common.agree.y')}</label>
              <input type="radio" id="infoNO" name="info" />
              <label htmlFor="infoNO">{t('common.agree.n')}</label>
            </div>
          </div>
          <div className="btn_center">
            <a href="#" onClick={handleCancel}>
              {t('label.button.cancel')}
            </a>
            <a href="#" className="blue" onClick={handleNext}>
              {t('label.button.next')}
            </a>
          </div>
        </article>
      </section>
      <CustomAlert
        contentText={customAlert.message}
        open={customAlert.open}
        handleAlert={customAlert.handleAlert}
      />
    </>
  )
}

export const getServerSideProps: GetServerSideProps = async context => {
  let policyTOS = {}
  let policyPP = {}

  try {
    const resultTOS = await policyService.getLatest('TOS')
    if (resultTOS && resultTOS.data) {
      policyTOS = (await resultTOS.data) as IPolicy
    }
    const resultPP = await policyService.getLatest('PP')
    if (resultPP && resultPP.data) {
      policyPP = (await resultPP.data) as IPolicy
    }
  } catch (error) {
    console.error(`posts item query error ${error.message}`)
  }

  return {
    props: {
      policyTOS,
      policyPP,
    },
  }
}

export default Join
