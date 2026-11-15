package com.example.exerciserecord

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

private data class Exercise(val name:String, val icon:String, val color:Long)
private val palette = listOf(0xFF4F7DF3,0xFF21A179,0xFFFF8A3D,0xFFE0528A,0xFF8B63D9,0xFF19A7CE,0xFFDAA520,0xFF64748B,0xFFEF5350,0xFF34495E)
private val iconOptions = listOf("fitness","run","bike","walk","swim","yoga","heart","star")
private fun iconFor(s:String):ImageVector = when(s) {
    "run" -> Icons.Default.DirectionsRun
    "bike" -> Icons.Default.DirectionsBike
    "walk" -> Icons.Default.DirectionsWalk
    "swim" -> Icons.Default.Pool
    "yoga" -> Icons.Default.SelfImprovement
    "heart" -> Icons.Default.Favorite
    "star" -> Icons.Default.Star
    else -> Icons.Default.FitnessCenter
}
private fun colorOf(v:Long)=Color(v)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs=getSharedPreferences("exercise_record", MODE_PRIVATE)
        setContent {
            var exercises by remember {
                mutableStateOf(run {
                    val raw=prefs.getString("exercises", null)
                    if(raw==null) listOf(Exercise("유산소","run",0xFFFF8A3D),Exercise("상체 운동","fitness",0xFF4F7DF3),Exercise("하체 운동","walk",0xFF21A179),Exercise("기타 운동","star",0xFF8B63D9))
                    else try { val a=JSONArray(raw); (0 until a.length()).map { val o=a.getJSONObject(it); Exercise(o.getString("name"),o.getString("icon"),o.getLong("color")) } } catch(_:Exception){ emptyList() }
                })
            }
            var selected by remember { mutableIntStateOf(-1) }
            var editName by remember { mutableStateOf("") }
            var editIcon by remember { mutableStateOf("fitness") }
            var editColor by remember { mutableLongStateOf(palette[0]) }
            var showEditor by remember { mutableStateOf(false) }
            var month by remember { mutableStateOf(YearMonth.now()) }
            var selectedDay by remember { mutableStateOf(LocalDate.now()) }
            var records by remember { mutableStateOf(prefs.getString("records","") ?: "") }
            fun persist(list:List<Exercise>) {
                exercises=list
                val a=JSONArray()
                list.forEach { a.put(JSONObject().put("name",it.name).put("icon",it.icon).put("color",it.color)) }
                prefs.edit().putString("exercises",a.toString()).apply()
            }
            MaterialTheme(colorScheme=lightColorScheme(primary=Color(0xFF4169E1),background=Color(0xFFF6F7FB),surface=Color.White)) {
                Surface(Modifier.fillMaxSize(), color=Color(0xFFF6F7FB)) {
                    Column(Modifier.fillMaxSize().padding(20.dp)) {
                        Row(verticalAlignment=Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("운동 기록",fontSize=27.sp,fontWeight=FontWeight.Bold,color=Color(0xFF182033))
                                Text("나의 운동을 꾸준히 기록해요",fontSize=13.sp,color=Color.Gray)
                            }
                            Icon(Icons.Default.CalendarMonth,null,tint=Color(0xFF4169E1),modifier=Modifier.size(30.dp))
                        }
                        Spacer(Modifier.height(20.dp))
                        Card(shape=RoundedCornerShape(22.dp),colors=CardDefaults.cardColors(containerColor=Color.White)) {
                            Column(Modifier.padding(16.dp)) {
                                Row(verticalAlignment=Alignment.CenterVertically) {
                                    IconButton(onClick={month=month.minusMonths(1)}) { Icon(Icons.Default.ChevronLeft,null) }
                                    Text("${month.year}년 ${month.monthValue}월",Modifier.weight(1f),fontSize=18.sp,fontWeight=FontWeight.Bold)
                                    IconButton(onClick={month=month.plusMonths(1)}) { Icon(Icons.Default.ChevronRight,null) }
                                }
                                Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceAround) {
                                    listOf("일","월","화","수","목","금","토").forEach { Text(it,color=Color.Gray,fontSize=12.sp,modifier=Modifier.width(32.dp)) }
                                }
                                val first=month.atDay(1)
                                val offset=first.dayOfWeek.value%7
                                val days=(1..month.lengthOfMonth()).toList()
                                val cells=List(offset){0}+days
                                cells.chunked(7).forEach { week ->
                                    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceAround) {
                                        week.forEach { d ->
                                            Box(Modifier.size(36.dp).background(if(d>0 && selectedDay==month.atDay(d)) Color(0xFFE8EEFF) else Color.Transparent,CircleShape).clickable(enabled=d>0){selectedDay=month.atDay(d)},contentAlignment=Alignment.Center) {
                                                if(d>0) Text(d.toString(),fontSize=13.sp,color=if(selectedDay==month.atDay(d)) Color(0xFF3159C9) else Color(0xFF293247),fontWeight=if(selectedDay==month.atDay(d)) FontWeight.Bold else FontWeight.Normal)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(18.dp))
                        Row(verticalAlignment=Alignment.CenterVertically) {
                            Text("운동 항목",fontSize=19.sp,fontWeight=FontWeight.Bold,modifier=Modifier.weight(1f))
                            TextButton(onClick={selected=-1;editName="";editIcon="fitness";editColor=palette[0];showEditor=true}) { Icon(Icons.Default.Add,null); Text("추가") }
                        }
                        LazyColumn(verticalArrangement=Arrangement.spacedBy(9.dp),modifier=Modifier.weight(1f)) {
                            items(exercises) { ex ->
                                Card(shape=RoundedCornerShape(16.dp),colors=CardDefaults.cardColors(containerColor=Color.White),modifier=Modifier.fillMaxWidth().clickable {
                                    selected=exercises.indexOf(ex);editName=ex.name;editIcon=ex.icon;editColor=ex.color;showEditor=true
                                }) {
                                    Row(Modifier.padding(14.dp),verticalAlignment=Alignment.CenterVertically) {
                                        Box(Modifier.size(44.dp).background(colorOf(ex.color).copy(alpha=.13f),CircleShape),contentAlignment=Alignment.Center) {
                                            Icon(iconFor(ex.icon),null,tint=colorOf(ex.color),modifier=Modifier.size(24.dp))
                                        }
                                        Text(ex.name,Modifier.weight(1f).padding(start=13.dp),fontWeight=FontWeight.SemiBold,fontSize=15.sp)
                                        Text("색상·아이콘 편집",fontSize=11.sp,color=Color.Gray)
                                        Icon(Icons.Default.ChevronRight,null,tint=Color.LightGray)
                                    }
                                }
                            }
                        }
                        Text("선택한 날짜: ${selectedDay.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))}",color=Color.Gray,fontSize=12.sp)
                    }
                }
            }
            if(showEditor) {
                AlertDialog(onDismissRequest={showEditor=false},title={Text(if(selected<0) "운동 항목 추가" else "운동 항목 편집")},text={
                    Column {
                        OutlinedTextField(value=editName,onValueChange={editName=it},label={Text("운동 이름")},singleLine=true)
                        Spacer(Modifier.height(14.dp))
                        Text("아이콘",fontWeight=FontWeight.SemiBold)
                        Row(horizontalArrangement=Arrangement.spacedBy(5.dp)) {
                            iconOptions.forEach { key ->
                                IconButton(onClick={editIcon=key}) {
                                    Icon(iconFor(key),null,tint=if(editIcon==key) Color(0xFF3159C9) else Color.Gray)
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("아이콘 색상",fontWeight=FontWeight.SemiBold)
                        Column(verticalArrangement=Arrangement.spacedBy(9.dp)) {
                            palette.chunked(5).forEach { row ->
                                Row(horizontalArrangement=Arrangement.spacedBy(13.dp)) {
                                    row.forEach { c ->
                                        Box(Modifier.size(31.dp).background(colorOf(c),CircleShape).clickable { editColor=c },contentAlignment=Alignment.Center) {
                                            if(editColor==c) Icon(Icons.Default.Check,null,tint=Color.White,modifier=Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                        Row(verticalAlignment=Alignment.CenterVertically) {
                            Icon(iconFor(editIcon),null,tint=colorOf(editColor),modifier=Modifier.size(28.dp))
                            Text(editName.ifBlank{"미리보기"},Modifier.padding(start=10.dp),fontWeight=FontWeight.Medium)
                        }
                    }
                },confirmButton={ TextButton(onClick={
                    if(editName.isNotBlank()) {
                        val list=exercises.toMutableList()
                        val item=Exercise(editName.trim(),editIcon,editColor)
                        if(selected<0) list.add(item) else if(selected<list.size) list[selected]=item
                        persist(list)
                    }
                    showEditor=false
                }) { Text("저장") } },dismissButton={
                    Row {
                        if(selected>=0) TextButton(onClick={val l=exercises.toMutableList();if(selected<l.size)l.removeAt(selected);persist(l);showEditor=false}){Text("삭제",color=Color.Red)}
                        TextButton(onClick={showEditor=false}){Text("취소")}
                    }
                })
            }
        }
    }
}
