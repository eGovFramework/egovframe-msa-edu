import { DetailButtons } from '@components/Buttons'
import {
  ReserveItemAdditional,
  ReserveItemBasic,
  ReserveItemManager,
} from '@components/ReserveItem'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import {
  ICode,
  ILocation,
  IReserveItem,
  locationService,
  reserveItemService,
} from '@service'
import { detailButtonsSnackAtom, errorStateSelector } from '@stores'
import produce from 'immer'
import { GetServerSideProps } from 'next'
import { useTranslation } from 'next-i18next'
import { useRouter } from 'next/router'
import React, { useEffect } from 'react'
import { FormProvider, useForm } from 'react-hook-form'
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
    card: {
      width: '100%',
    },
    cardActions: {
      justifyContent: 'center',
    },
    switch: {
      width: '100%',
      justifyContent: 'start',
      border: '1px solid rgba(0, 0, 0, 0.23)',
      borderRadius: theme.spacing(0.5),
      padding: theme.spacing(1),
      marginTop: theme.spacing(1),
    },
  }),
)

interface ReserveItemDetailProps {
  reserveItemId: string
  initData?: IReserveItem
  locations: ILocation[]
  categories: ICode[]
  reserveMethods: ICode[]
  reserveMeans: ICode[]
  selectionMeans: ICode[]
  targets: ICode[]
}

const ReserveItemDetail = (props: ReserveItemDetailProps) => {
  const { reserveItemId, initData, targets, ...rest } = props
  const classes = useStyles()
  const route = useRouter()
  const { t } = useTranslation()

  //form hook
  const methods = useForm<IReserveItem>({
    defaultValues: initData,
  })

  const { register, formState, control, handleSubmit, setFocus, getValues } =
    methods

  //상태관리 hook
  const setErrorState = useSetRecoilState(errorStateSelector)
  // <목록, 저장> 버튼 component 상태 전이
  const setSuccessSnackBar = useSetRecoilState(detailButtonsSnackAtom)

  useEffect(() => {
    if (formState.errors) {
      setFocus('reserveItemName')
    }
  }, [formState.errors])

  const handleSave = async (formData: IReserveItem) => {
    setSuccessSnackBar('loading')
    try {
      formData = produce(formData, draft => {
        draft.isPaid = Boolean(draft.isPaid)
        draft.isPeriod = Boolean(draft.isPeriod)
        draft.isUse = Boolean(draft.isUse)
      })

      let result
      if (reserveItemId === '-1') {
        formData = produce(formData, draft => {
          draft.inventoryQty = draft.totalQty
        })
        result = await reserveItemService.save(formData)
      } else {
        formData = produce(formData, draft => {
          draft.inventoryQty =
            draft.totalQty - draft.prevTotalQty + draft.inventoryQty
        })
        result = await reserveItemService.update(
          parseInt(reserveItemId),
          formData,
        )
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

  const handleList = () => {
    route.push('/reserve-item')
  }

  return (
    <div className={classes.root}>
      <FormProvider {...methods}>
        <ReserveItemBasic
          control={control}
          formState={formState}
          register={register}
          getValues={getValues}
          data={initData}
          {...rest}
        />
        <ReserveItemAdditional
          control={control}
          formState={formState}
          targets={targets}
        />
        <ReserveItemManager control={control} formState={formState} />

        <DetailButtons
          handleSave={handleSubmit(handleSave)}
          handleList={handleList}
        />
      </FormProvider>
    </div>
  )
}

export const getServerSideProps: GetServerSideProps = async ({ query }) => {
  const reserveItemId = query.id as string

  let locations: ILocation[] = []
  let categories: ICode[] = []
  let reserveMethods: ICode[] = []
  let reserveMeans: ICode[] = []
  let selectionMeans: ICode[] = []
  let targets: ICode[] = []

  try {
    locations = await (await locationService.getList()).data
    categories = await (
      await reserveItemService.getCode('reserve-category')
    ).data
    reserveMethods = await (
      await reserveItemService.getCode('reserve-method')
    ).data
    reserveMeans = await (
      await reserveItemService.getCode('reserve-means')
    ).data
    selectionMeans = await (
      await reserveItemService.getCode('reserve-selection')
    ).data
    targets = await (await reserveItemService.getCode('reserve-target')).data
  } catch (error) {
    console.error(`reserve item query error ${error.message}`)
  }

  if (reserveItemId === '-1') {
    return {
      props: {
        reserveItemId,
        categories,
        locations,
        reserveMethods,
        reserveMeans,
        selectionMeans,
        targets,
      },
    }
  }

  let data = {}

  try {
    const result = await reserveItemService.get(parseInt(reserveItemId))

    if (result) {
      data = (await result.data) as IReserveItem
    }
  } catch (error) {
    console.error(`reserve item query error ${error.message}`)
    if (error.response?.data?.code === 'E003') {
      return {
        notFound: true,
      }
    }
  }

  return {
    props: {
      reserveItemId,
      initData: data,
      categories,
      locations,
      reserveMethods,
      reserveMeans,
      selectionMeans,
      targets,
    },
  }
}

export default ReserveItemDetail
