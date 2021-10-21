import React from 'react'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import {
  Bar,
  BarChart as ReBarChart,
  CartesianGrid,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts'
import Paper from '@material-ui/core/Paper'
import Typography from '@material-ui/core/Typography'

const MAB_BAR_THICKNESS = 50

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    paper: {
      marginTop: theme.spacing(1),
      marginBottom: theme.spacing(1),
      padding: theme.spacing(2),
      width: '100%',
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
    },
    title: {
      marginBottom: theme.spacing(1),
    },
  }),
)

interface BarChartProps {
  data: any
  id: string
  tooltipContent?: ({ active, payload, label }) => React.ReactNode
  title?: string
  handleCellClick?: (data, index) => void
  customxAxisTick?: (value: any, index: number) => string
}

const CustomBarChart = ({
  data,
  id,
  tooltipContent,
  title,
  handleCellClick,
  customxAxisTick,
}: BarChartProps) => {
  const classes = useStyles()

  return (
    <Paper variant="outlined" className={classes.paper}>
      {title && (
        <Typography className={classes.title} variant="h4">
          {title}
        </Typography>
      )}
      <ResponsiveContainer width="90%" height={400}>
        <ReBarChart
          key={id}
          data={data}
          margin={{ top: 20, right: 30, left: 0, bottom: 0 }}
        >
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="x" tickFormatter={customxAxisTick} />
          <YAxis />
          <Tooltip cursor={{ fill: 'transparent' }} content={tooltipContent} />
          <Bar
            dataKey="y"
            maxBarSize={MAB_BAR_THICKNESS}
            stroke="#8884d8"
            fill="#8884d8"
            type="monotone"
            onClick={handleCellClick}
          />
        </ReBarChart>
      </ResponsiveContainer>
    </Paper>
  )
}

export default CustomBarChart
