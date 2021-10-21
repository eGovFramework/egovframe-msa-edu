import { DetailButtons } from '@components/Buttons'
import DialogPopup from '@components/DialogPopup'
import {
  ReserveClientInfo,
  ReserveInfo,
  ReserveInfoView,
  ReserveItemInfo,
} from '@components/Reserve'
import { UploadType } from '@components/Upload'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import ReserveItem from '@pages/reserve-item'
import {
  fileService,
  IAttachmentResponse,
  ICode,
  IReserve,
  IReserveItem,
  IReserveItemRelation,
  reserveItemService,
  ReserveSavePayload,
  reserveService,
} from '@service'
import { detailButtonsSnackAtom, errorStateSelector } from '@stores'
import { GetServerSideProps } from 'next'
import { useRouter } from 'next/router'
import React, { useEffect, useRef, useState } from 'react'
import { FormProvider, useForm } from 'react-hook-form'
import { useTranslation } from 'react-i18next'
import { useSetRecoilState } from 'recoil'

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      flexGrow: 1,
      marginTop: theme.spacing(1),
      '& .MuiOutlinedInput-input': {
        padding: theme.spacing(2),
      },
    },
  }),
)

interface ReserveDetailProps {
  reserveId?: string
  initData?: IReserve
  reserveItem?: IReserveItemRelation
  status?: ICode[]
}

const ReserveDetail = (props: ReserveDetailProps) => {
  const { reserveId, reserveItem, initData, status } = props
  const classes = useStyles()
  const router = useRouter()

  const { t } = useTranslation()
  const uploadRef = useRef<UploadType>()

  // 상태관리 hook
  const setErrorState = useSetRecoilState(errorStateSelector)
  const setSuccessSnackBar = useSetRecoilState(detailButtonsSnackAtom)

  //form hook
  const methods = useForm<IReserve>({
    defaultValues: initData,
  })
  const {
    register,
    formState,
    control,
    handleSubmit,
    clearErrors,
    getValues,
    setValue,
    setError,
  } = methods

  const [item, setItem] = useState<IReserveItemRelation>(undefined)
  const [dialogOpen, setDialogOpen] = useState<boolean>(false)
  const [attachData, setAttachData] = useState<
    IAttachmentResponse[] | undefined
  >(undefined)

  useEffect(() => {
    if (initData?.attachmentCode) {
      fileService
        .getAttachmentList(initData.attachmentCode)
        .then(result => {
          if (result?.data) {
            setAttachData(result.data)
          }
        })
        .catch(error => setErrorState({ error }))
    }
  }, [initData])

  useEffect(() => {
    if (reserveItem) {
      setItem(reserveItem)
    }
  }, [reserveItem])

  const handlePopup = async (data: IReserveItem) => {
    if (data) {
      try {
        const result = await reserveItemService.getWithRelation(
          data.reserveItemId,
        )
        if (result) {
          setItem(result.data)
          clearErrors()
        }
      } catch (error) {
        setErrorState({ error })
      }
    }

    handleDialogClose()
  }

  const handleDialogOpen = () => {
    setDialogOpen(true)
  }

  const handleDialogClose = () => {
    setDialogOpen(false)
  }

  const handleSave = async (formData: IReserve) => {
    setSuccessSnackBar('loading')
    let attachCode = initData?.attachmentCode
    try {
      attachCode = await uploadRef.current?.upload(
        {
          entityName: 'reserve',
          entityId: null,
        },
        attachData,
      )

      // 관리자가 예약하는 경우 심사/실시간 할 것없이 무조건 예약확정(status=approve)
      const saveData: ReserveSavePayload = {
        ...formData,
        reserveItemId: item.reserveItemId,
        reserveStatusId: 'approve',
        locationId: item.locationId,
        categoryId: item.categoryId,
        attachmentCode: attachCode === 'no attachments' ? null : attachCode,
      }

      let result
      if (reserveId === '-1') {
        result = await reserveService.save(saveData)
      } else {
        result = await reserveService.update(reserveId, saveData)
      }

      if (result) {
        setSuccessSnackBar('success')

        handleList()
      }
    } catch (error) {
      setSuccessSnackBar('none')
      setErrorState({ error })
      if (reserveId === '-1') {
        // 저장 실패한 경우 첨부파일 rollback
        uploadRef.current?.rollback(attachCode)
      }
    }
  }

  const handleList = () => {
    router.push('/reserve')
  }

  const handleButtonStatus = async (status: string, reason?: string) => {
    setSuccessSnackBar('loading')
    try {
      let result
      if (status === 'cancel') {
        result = await reserveService.cancel(reserveId, reason)
      } else {
        result = await reserveService.approve(reserveId)
      }
      if (result) {
        setSuccessSnackBar('success')

        handleList()
      }
    } catch (error) {
      setSuccessSnackBar('none')
      setErrorState({ error })
    }
  }

  return (
    <div className={classes.root}>
      {item && (
        <ReserveItemInfo
          data={item}
          handleSearchItem={handleDialogOpen}
          reserveStatus={status.find(
            code => code.codeId === initData?.reserveStatusId,
          )}
        />
      )}
      <DialogPopup
        id="find-dialog"
        handleClose={handleDialogClose}
        open={dialogOpen}
        title={`${t('reserve_item')} ${t('label.button.find')}`}
      >
        <ReserveItem handlePopup={handlePopup} />
      </DialogPopup>
      {initData?.reserveStatusId ? (
        <>
          <ReserveInfoView
            data={initData}
            handleList={handleList}
            handleButtons={handleButtonStatus}
            attachData={attachData}
          />
        </>
      ) : (
        <FormProvider {...methods}>
          {item && (
            <ReserveInfo
              control={control}
              formState={formState}
              register={register}
              getValues={getValues}
              data={initData}
              item={item}
              setError={setError}
              clearErrors={clearErrors}
              fileProps={{
                uploadRef,
                attachData,
                setAttachData,
              }}
            />
          )}
          <ReserveClientInfo
            control={control}
            formState={formState}
            register={register}
            getValues={getValues}
            data={initData}
            setValue={setValue}
          />
          <DetailButtons
            handleSave={handleSubmit(handleSave)}
            handleList={handleList}
          />
        </FormProvider>
      )}
    </div>
  )
}

export const getServerSideProps: GetServerSideProps = async ({ query }) => {
  const { id, reserveItemId } = query
  let initData: IReserve = null
  let reserveItem: IReserveItemRelation = null
  let status: ICode = null

  try {
    status = await (await reserveItemService.getCode('reserve-status')).data

    if (id === '-1') {
      const result = await reserveItemService.getWithRelation(
        parseInt(reserveItemId as string),
      )
      if (result) {
        reserveItem = result.data
      }
    } else {
      const result = await reserveService.get(id as string)
      if (result) {
        initData = result.data
        reserveItem = initData.reserveItem
      }
    }
  } catch (error) {
    console.error(
      `reserve detail server side props error occur : ${error.message}`,
    )
    if (error.response?.data?.code === 'E003') {
      return {
        notFound: true,
      }
    }
  }

  return {
    props: {
      reserveId: id,
      initData,
      reserveItem,
      status,
    },
  }
}

export default ReserveDetail
