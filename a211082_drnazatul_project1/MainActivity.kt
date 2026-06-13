package com.example.a211082_drnazatul_project1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.a211082_drnazatul_project1.data.OnRoadUiState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                val viewModel: OnRoadViewModel = viewModel()

                NavHost(navController = navController, startDestination = "map") {
                    composable("map") { MapScreen(viewModel, navController) }
                    composable("summary") { SummaryScreen(viewModel, navController) }
                    composable("saved") { SavedPlacesScreen(viewModel, navController) }
                    composable("add_place") { AddPlaceScreen(viewModel, navController) }
                    composable("profile") { ProfileScreen(viewModel, navController) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(viewModel: OnRoadViewModel, navController: NavHostController) {
    val uiState = viewModel.uiState
    var searchInput by remember { mutableStateOf("") }
    var showBottomSheet by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.maps),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(modifier = Modifier.fillMaxWidth().padding(top = 60.dp, start = 16.dp, end = 16.dp)) {
            ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(32.dp)) {
                Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Search, null, tint = Color.Gray)
                    BasicTextField(
                        value = searchInput,
                        onValueChange = { searchInput = it },
                        modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
                        decorationBox = { if (searchInput.isEmpty()) Text("Search destination...", color = Color.Gray); it() }
                    )
                    IconButton(onClick = { if (searchInput.isNotBlank()) viewModel.updateDestination(searchInput, true) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.width(40.dp).height(4.dp).background(Color.LightGray, RoundedCornerShape(2.dp))
                        .clickable { showBottomSheet = true }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    if (uiState.isRouting) {
                        Text("Heading to ${uiState.destination}", fontWeight = FontWeight.Bold)
                        Button(onClick = { navController.navigate("summary") }, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                            Text("Start Navigation")
                        }
                    } else {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { showBottomSheet = true }) {
                                Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.primary)
                                Text("Explore", fontSize = 12.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { navController.navigate("saved") }) {
                                Icon(Icons.Default.FavoriteBorder, null)
                                Text("Saved", fontSize = 12.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { navController.navigate("profile") }) {
                                Icon(Icons.Default.Person, null)
                                Text("Profile", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(onDismissRequest = { showBottomSheet = false }) {
                Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                    Text("Explore Nearby Places", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("• McDonald's UKM")
                    Text("• Library UKM")
                    Text("• CIMB Bank")
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}

@Composable
fun SummaryScreen(viewModel: OnRoadViewModel, navController: NavHostController) {
    val uiState = viewModel.uiState
    val dest = uiState.destination
    val isSaved = uiState.savedPlaces.contains(dest)

    Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(100.dp))
        Text("Trip Finished!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

        ElevatedCard(modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(dest, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                IconButton(onClick = { viewModel.toggleSavePlace(dest) }) {
                    Icon(if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder, null, tint = if (isSaved) Color.Red else Color.Gray)
                }
            }
        }
        Button(onClick = { viewModel.setRouting(false); navController.popBackStack("map", false) }, modifier = Modifier.fillMaxWidth()) {
            Text("Back to Home")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedPlacesScreen(viewModel: OnRoadViewModel, navController: NavHostController) {
    val uiState = viewModel.uiState
    Scaffold(
        topBar = { TopAppBar(title = { Text("Saved Places") }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }) },
        floatingActionButton = { FloatingActionButton(onClick = { navController.navigate("add_place") }) { Icon(Icons.Default.Add, null) } }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
            items(uiState.savedPlaces) { place ->
                ListItem(headlineContent = { Text(place) }, modifier = Modifier.clickable { viewModel.updateDestination(place, true); navController.popBackStack() })
            }
        }
    }
}

@Composable
fun AddPlaceScreen(viewModel: OnRoadViewModel, navController: NavHostController) {
    var name by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Add New Place", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Place Name") }, modifier = Modifier.fillMaxWidth().padding(top = 16.dp))
        Button(onClick = { viewModel.addPlace(name); navController.popBackStack() }, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) { Text("Save") }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: OnRoadViewModel, navController: NavHostController) {
    val savedCount = viewModel.uiState.savedPlaces.size

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(100.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Icon(Icons.Default.Person, null, modifier = Modifier.padding(20.dp), tint = MaterialTheme.colorScheme.onSecondaryContainer)
            }

            Text("User OnRoad", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
            Text("Explore the journey with ease!", color = Color.Gray)

            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("24", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text("Trips", fontSize = 12.sp, color = Color.Gray)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$savedCount", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
                    Text("Saved", fontSize = 12.sp, color = Color.Gray)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp))

            ListItem(headlineContent = { Text("Travel History") }, leadingContent = { Icon(Icons.AutoMirrored.Filled.List, null) })
            ListItem(headlineContent = { Text("Settings") }, leadingContent = { Icon(Icons.Default.Settings, null) })
            ListItem(headlineContent = { Text("Logout", color = Color.Red) }, leadingContent = { Icon(Icons.AutoMirrored.Filled.ExitToApp, null, tint = Color.Red) })
        }
    }
}