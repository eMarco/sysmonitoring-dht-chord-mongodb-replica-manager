import { CPUStat } from './model/cpu-stat';
import { RAMStat } from './model/ram-stat';
import { UptimeStat } from './model/uptime-stat';

export const CPUSTATS: CPUStat[] = [
  { usage: 0.5, timestamp: 6, scannerId: "asd", key: { key:"1699d6b5508374cf2becc8778548b263271da293"} },
  { usage: 0.2, timestamp: 4, scannerId: "asd", key: { key:"1699d6b5508374cf2becc8778548b263271da293"} },
  { usage: 0.1, timestamp: 3, scannerId: "asd", key: { key:"1699d6b5508374cf2becc8778548b263271da293"} }
]

export const MEMSTATS: RAMStat[] = [
  { MemTotal: 12126164, MemFree:230924, MemAvailable:838236, timestamp: 1517777828, className: "org.unict.ing.pds.dhtdb.utils.model.RAMStat", scannerId: "1" }
]

export const UPTIMESTATS: UptimeStat[] = [
  {seconds: 17, minutes:7, hours:6, days: 0, timestamp: 1517778670, className: "org.unict.ing.pds.dhtdb.utils.model.UptimeStat", scannerId: "1"}
]
/*



[
  {disk:sda, WritekBps:313.87, ReadkBps:694.29, timestamp: 1517780002, className: org.unict.ing.pds.dhtdb.utils.model.IOStat },
  {disk:sdb, WritekBps:13.16, ReadkBps:44.46, timestamp: 1517780002, className: org.unict.ing.pds.dhtdb.utils.model.IOStat },
  {disk:dm-0, WritekBps:305.58, ReadkBps:637.26, timestamp: 1517780002, className: org.unict.ing.pds.dhtdb.utils.model.IOStat },
  {disk:dm-1, WritekBps:0.23, ReadkBps:0.00, timestamp: 1517780002, className: org.unict.ing.pds.dhtdb.utils.model.IOStat },
  {disk:dm-2, WritekBps:7.92, ReadkBps:57.04, timestamp: 1517780002, className: org.unict.ing.pds.dhtdb.utils.model.IOStat }
],
[
  {disk:sda, WritekBps:315.71, ReadkBps:692.67, timestamp: 1517779859},
  {disk:sdb, WritekBps:13.24, ReadkBps:43.04, timestamp: 1517779859},
  {disk:dm-0, WritekBps:307.38, ReadkBps:635.28, timestamp: 1517779859},
  {disk:dm-1, WritekBps:0.23, ReadkBps:0.00, timestamp: 1517779859},
  {disk:dm-2, WritekBps:7.96, ReadkBps:57.39, timestamp: 1517779859}
],
*/
