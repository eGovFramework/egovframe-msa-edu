import CustomBarChart from '@components/CustomBarChart'
import { format as dateFormat, getCurrentDate } from '@libs/date'
import { Card, CardContent, Typography } from '@material-ui/core'
import MenuItem from '@material-ui/core/MenuItem'
import Select from '@material-ui/core/Select'
import { DailyPayload, ISite, statisticsService } from '@service'
import { GetServerSideProps } from 'next'
import React, { useState } from 'react'
import { useTranslation } from 'react-i18next'

const initDailyPayload: DailyPayload = {
  year: parseInt(dateFormat(getCurrentDate(), 'yyyy')),
  month: parseInt(dateFormat(getCurrentDate(), 'MM')),
}

const tooltipContent = tooltip => (
  <Card variant="outlined">
    <CardContent>
      <Typography variant="h5">{tooltip}</Typography>
    </CardContent>
  </Card>
)

interface StatisticsProps {
  sites: ISite[]
}

function Statistics(props: StatisticsProps) {
  const { sites } = props
  const { t } = useTranslation()

  const [siteState, setSiteState] = useState<number>(sites[0]?.id)
  const [dailyPayload, setDailyPayload] =
    useState<DailyPayload>(initDailyPayload)

  const { monthly } = statisticsService.getMonthly(siteState)
  const { daily } = statisticsService.getDaily(siteState, dailyPayload)

  const handleSiteChange = (event: React.ChangeEvent<{ value: unknown }>) => {
    setSiteState(event.target.value as number)
    setDailyPayload(initDailyPayload)
  }

  const handleMonthlyClick = (data, index) => {
    if (data) {
      setDailyPayload({
        year: data.year,
        month: data.month,
      })
    }
  }

  const monthlyTooltipContent = ({ active, payload, label }) => {
    if (!active || !payload || !label) return null

    return tooltipContent(
      `${label} ${t('statistics.month')} : ${payload[0].value}`,
    )
  }

  const dailyTooltipContent = ({ active, payload, label }) => {
    if (!active || !payload || !label) return null

    return tooltipContent(
      `${label} ${t('statistics.day')} : ${payload[0].value}`,
    )
  }

  return (
    <div>
      <Select
        variant="outlined"
        fullWidth
        value={siteState}
        onChange={handleSiteChange}
      >
        {sites?.map(item => (
          <MenuItem key={item.id} value={item.id}>
            {item.name}
          </MenuItem>
        ))}
      </Select>
      {monthly && (
        <CustomBarChart
          id="monthlyChart"
          data={monthly}
          tooltipContent={monthlyTooltipContent}
          title={`${dailyPayload.year}${t('statistics.year')} ${t(
            'statistics.monthly',
          )} ${t('statistics.access')}`}
          handleCellClick={handleMonthlyClick}
          customxAxisTick={(value: any, index: number) => {
            return `${value} ${t('statistics.month')}`
          }}
        />
      )}

      {daily && (
        <CustomBarChart
          id="dailyChart"
          data={daily}
          tooltipContent={dailyTooltipContent}
          title={`${dailyPayload.year}${t('statistics.year')} ${
            dailyPayload.month
          }${t('statistics.month')} ${t('statistics.daily')} ${t(
            'statistics.access',
          )}`}
          customxAxisTick={(value: any, index: number) => {
            return `${value} ${t('statistics.day')}`
          }}
        />
      )}
    </div>
  )
}

export const getServerSideProps: GetServerSideProps = async context => {
  let sites: ISite[] = []

  try {
    const result = await statisticsService.getSites()

    if (result) {
      sites = result.data
    }
  } catch (error) {
    console.error(`statistics  getServerSideProps error ${error.message}`)
  }

  return {
    props: {
      sites,
    },
  }
}

export default Statistics
