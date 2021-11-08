import { BottomButtons, IButtons } from '@components/Buttons'
import Upload, { UploadType } from '@components/Upload'
import { DLWrapper } from '@components/WriteDLFields'
import { DEFAULT_ERROR_MESSAGE } from '@constants'
import { convertStringToDateFormat } from '@libs/date'
import Backdrop from '@material-ui/core/Backdrop'
import CircularProgress from '@material-ui/core/CircularProgress'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import { IReserveComplete } from '@pages/reserve/[category]/[id]'
import {
  IReserve,
  IReserveItem,
  ReserveSavePayload,
  reserveService,
  UploadInfoReqeust,
} from '@service'
import { errorStateSelector, userAtom } from '@stores'
import produce from 'immer'
import { useRouter } from 'next/router'
import React, { useCallback, useEffect, useMemo, useRef, useState } from 'react'
import 'react-datepicker/dist/react-datepicker.css'
import {
  Control,
  Controller,
  FormProvider,
  FormState,
  useForm,
  useWatch,
} from 'react-hook-form'
import { useTranslation } from 'react-i18next'
import { useRecoilValue, useSetRecoilState } from 'recoil'
import { ReserveDateRangeField } from './ReserveDateRangeField'
import ReserveEventSource from './ReserveEventSource'

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    backdrop: {
      zIndex: theme.zIndex.drawer + 1,
      color: '#fff',
    },
  }),
)

interface ReserveEditProps {
  reserveItem: IReserveItem
  setComplete: React.Dispatch<React.SetStateAction<IReserveComplete>>
}

export interface ReserveEditFormProps {
  control: Control<ReserveSavePayload>
  formState: FormState<ReserveSavePayload>
  required?: boolean
}

const ReserveEdit = (props: ReserveEditProps) => {
  const classes = useStyles()
  const { reserveItem, setComplete } = props
  const router = useRouter()
  const { t, i18n } = useTranslation()

  const user = useRecoilValue(userAtom)

  const setErrorState = useSetRecoilState(errorStateSelector)
  const [loading, setLoading] = useState<boolean>(false)
  const [currentInventoryQty, setCurrentInventoryQty] = useState<number | null>(
    reserveItem.inventoryQty || null,
  )

  const [reserve, setReserve] = useState<
    { reserve: IReserve; category: string } | undefined
  >(undefined)
  const [isEvent, setEvents] = useState<boolean>(false)

  const methods = useForm<ReserveSavePayload>()
  const { control, formState, setError, clearErrors, handleSubmit } = methods

  const uploadRef = useRef<UploadType>()

  const watchStartDate = useWatch({
    control,
    name: 'reserveStartDate',
  })

  const watchEndDate = useWatch({
    control,
    name: 'reserveEndDate',
  })

  const watchQty = useWatch({
    control,
    name: 'reserveQty',
  })

  useEffect(() => {
    if (watchQty && currentInventoryQty) {
      if (watchQty > currentInventoryQty) {
        setError(
          'reserveQty',
          { message: '현재가능수량보다 많습니다.' },
          { shouldFocus: true },
        )
      } else {
        clearErrors('reserveQty')
      }
    }
  }, [watchQty, currentInventoryQty])

  useEffect(() => {
    if (watchStartDate && watchEndDate) {
      if (reserveItem.categoryId === 'equipment') {
        getCountInventory()
      }
    }
  }, [watchStartDate, watchEndDate])

  const getCountInventory = useCallback(async () => {
    try {
      const result = await reserveService.getCountInventory(
        reserveItem.reserveItemId,
        convertStringToDateFormat(watchStartDate),
        convertStringToDateFormat(watchEndDate),
      )
      if (result) {
        setCurrentInventoryQty(result.data)
      }
    } catch (error) {
      setErrorState({ error })
    }
  }, [reserveItem.reserveItemId, watchStartDate, watchEndDate])

  const successCallback = useCallback(() => {
    setComplete({
      done: true,
      reserveId: reserve.reserve.reserveId,
    })
    setLoading(false)
  }, [reserve])

  useEffect(() => {
    if (reserve && reserve.category !== 'education') {
      successCallback()
    }
  }, [reserve])

  const errorCallback = useCallback(
    (errors: any, attachmentCode: string) => {
      setErrorState(errors)
      setLoading(false)
      if (attachmentCode) {
        uploadRef.current.rollback(attachmentCode)
      }
    },
    [uploadRef],
  )

  /**
   * 심사인 경우 저장
   */
  const saveEvaluate = async (formData: ReserveSavePayload) => {
    try {
      const result = await reserveService.createAudit(formData)
      if (result) {
        setReserve({
          reserve: result.data,
          category: formData.categoryId,
        })
      } else {
        errorCallback(
          { message: DEFAULT_ERROR_MESSAGE },
          formData.attachmentCode,
        )
      }
    } catch (error) {
      errorCallback({ error }, formData.attachmentCode)
    }
  }

  /**
   * 실시간 & 선착순 인 경우 저장 후 이벤트 메세지까지 완료되었는지 확인
   */
  const save = async (formData: ReserveSavePayload) => {
    try {
      const result = await reserveService.create(formData)
      if (result) {
        setReserve({
          reserve: result.data,
          category: formData.categoryId,
        })
        if (formData.categoryId === 'education') {
          setEvents(true)
        }
      } else {
        errorCallback(
          { message: DEFAULT_ERROR_MESSAGE },
          formData.attachmentCode,
        )
      }
    } catch (error) {
      errorCallback({ error }, formData.attachmentCode)
    }
  }

  const handleSavebefore = async (formData: ReserveSavePayload) => {
    setLoading(true)
    let attachmentCode = ''
    try {
      const info: UploadInfoReqeust = {
        entityName: 'reserve',
        entityId: '-1',
      }

      const result = await uploadRef.current?.upload(info)
      if (result !== 'no attachments' && result !== 'no update list') {
        attachmentCode = result
      }
    } catch (error) {
      setErrorState({ error })
    }

    formData = produce(formData, draft => {
      draft.reserveItemId = reserveItem.reserveItemId
      draft.locationId = reserveItem.locationId
      draft.categoryId = reserveItem.categoryId
      draft.totalQty = reserveItem.totalQty
      draft.reserveMethodId = reserveItem.reserveMethodId
      draft.reserveMeansId = reserveItem.reserveMeansId
      draft.operationStartDate = reserveItem.operationEndDate
      draft.operationEndDate = reserveItem.operationEndDate
      draft.requestStartDate = reserveItem.requestStartDate
      draft.requestEndDate = reserveItem.requestEndDate
      draft.isPeriod = reserveItem.isPeriod
      draft.periodMaxCount = reserveItem.periodMaxCount
      draft.attachmentCode = attachmentCode
      draft.userId = user.userId
      draft.userEmail = user.email
      if (draft.categoryId !== 'education') {
        draft.reserveStartDate = convertStringToDateFormat(
          draft.reserveStartDate,
          "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        )
        draft.reserveEndDate = convertStringToDateFormat(
          draft.reserveEndDate,
          "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        )
      }
    })

    if (
      reserveItem.reserveMeansId === 'realtime' &&
      reserveItem.selectionMeansId === 'fcfs'
    ) {
      save(formData)
    } else {
      saveEvaluate(formData)
    }
  }

  // 버튼
  const bottomButtons = useMemo((): IButtons[] => {
    const buttons: IButtons[] = []

    buttons.push({
      id: 'item-edit-button',
      title: t('reserve_item.request'),
      href: '',
      className: 'blue',
      handleClick: handleSubmit(handleSavebefore),
    })
    buttons.push({
      id: 'item-list-button',
      title: t('label.button.list'),
      href: `/reserve/${router.query.category}`,
    })

    return buttons
  }, [t, router.query])
  return (
    <>
      <FormProvider {...methods}>
        <h4>{`${t('reserve')} ${t('common.information')}`}</h4>
        <div className="view">
          <span>{t('common.required_fields')}</span>

          {reserveItem.categoryId === 'education' ? null : (
            <ReserveDateRangeField
              control={control}
              formState={formState}
              required={true}
            />
          )}

          {reserveItem.categoryId === 'space' ? null : (
            <Controller
              control={control}
              name="reserveQty"
              render={({ field, fieldState }) => (
                <DLWrapper
                  title={`${t('reserve.request')} ${
                    reserveItem.categoryId === 'equipment'
                      ? t('reserve.count')
                      : t('reserve.number_of_people')
                  }`}
                  required
                  error={fieldState.error}
                >
                  <input
                    type="text"
                    value={field.value}
                    onChange={field.onChange}
                    title={`${t('reserve.request')} ${
                      reserveItem.categoryId === 'equipment'
                        ? t('reserve.count')
                        : t('reserve.number_of_people')
                    }`}
                  />
                  {reserveItem.categoryId === 'equipment' ? (
                    <span className="span">
                      {`* ${t(
                        'reserve.msg.possible_count',
                      )} : ${currentInventoryQty} `}
                    </span>
                  ) : null}
                </DLWrapper>
              )}
              defaultValue={0}
              rules={{
                required: true,
                validate: value =>
                  value < currentInventoryQty || '현재 가능 수량보다 많습니다.',
              }}
            />
          )}

          <Controller
            control={control}
            name="reservePurposeContent"
            render={({ field, fieldState }) => (
              <DLWrapper
                title={`${t('reserve.request')} ${t('reserve.purpose')}`}
                className="inputTitle"
                required
                error={fieldState.error}
              >
                <input
                  type="text"
                  value={field.value}
                  onChange={field.onChange}
                  placeholder={`${t('reserve.request')} ${t(
                    'reserve.purpose',
                  )}${t('msg.placeholder')}`}
                />
              </DLWrapper>
            )}
            defaultValue=""
            rules={{ required: true }}
          />

          <Controller
            control={control}
            name="userContactNo"
            render={({ field, fieldState }) => (
              <DLWrapper
                title={`${t('reserve.user')} ${t('common.contact')}`}
                className="inputTitle"
                required
                error={fieldState.error}
              >
                <input
                  type="text"
                  value={field.value}
                  onChange={field.onChange}
                  placeholder={`${t('reserve.user')} ${t('common.contact')}${t(
                    'msg.placeholder',
                  )}`}
                />
              </DLWrapper>
            )}
            defaultValue=""
            rules={{ required: true }}
          />

          <dl>
            <dt>{t('common.attachment')}</dt>
            <dd>
              <Upload ref={uploadRef} multi />
            </dd>
          </dl>
        </div>

        <BottomButtons handleButtons={bottomButtons} />
      </FormProvider>
      <Backdrop className={classes.backdrop} open={loading}>
        <CircularProgress color="inherit" />
      </Backdrop>
      {reserve && isEvent && (
        <ReserveEventSource
          data={reserve.reserve}
          successCallback={successCallback}
          errorCallback={errorCallback}
        />
      )}
    </>
  )
}

export { ReserveEdit }
