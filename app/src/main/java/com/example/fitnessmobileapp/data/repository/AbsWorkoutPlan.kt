package com.example.fitnessmobileapp.data.repository

import com.example.fitnessmobileapp.data.model.PlanDay

object AbsWorkoutPlan {

    fun getThirtyDayPlan(): List<PlanDay> {
        return listOf(
            PlanDay(
                dayNumber = 1,
                title = "Ngày 1",
                exerciseCount = 7,
                durationMinutes = 6,
                isRestDay = false,
                exerciseType = "abs",
                exerciseIds = listOf(
                    "abs_001",
                    "abs_002",
                    "abs_003",
                    "abs_004",
                    "abs_005",
                    "abs_006",
                    "abs_007"
                )
            ),
            PlanDay(
                dayNumber = 2,
                title = "Ngày 2",
                exerciseCount = 7,
                durationMinutes = 6,
                isRestDay = false,
                exerciseType = "abs",
                exerciseIds = listOf(
                    "abs_008",
                    "abs_009",
                    "abs_010",
                    "abs_011",
                    "abs_012",
                    "abs_013",
                    "abs_014"
                )
            ),
            PlanDay(
                dayNumber = 3,
                title = "Ngày 3",
                exerciseCount = 7,
                durationMinutes = 7,
                isRestDay = false,
                exerciseType = "abs",
                exerciseIds = listOf(
                    "abs_001",
                    "abs_003",
                    "abs_005",
                    "abs_007",
                    "abs_015",
                    "abs_017",
                    "abs_018"
                )
            ),
            PlanDay(
                dayNumber = 4,
                title = "Ngày 4",
                exerciseCount = 0,
                durationMinutes = 0,
                isRestDay = true,
                exerciseType = "abs",
                exerciseIds = emptyList()
            ),
            PlanDay(
                dayNumber = 5,
                title = "Ngày 5",
                exerciseCount = 7,
                durationMinutes = 7,
                isRestDay = false,
                exerciseType = "abs",
                exerciseIds = listOf(
                    "abs_002",
                    "abs_004",
                    "abs_006",
                    "abs_008",
                    "abs_011",
                    "abs_013",
                    "abs_014"
                )
            ),
            PlanDay(
                dayNumber = 6,
                title = "Ngày 6",
                exerciseCount = 7,
                durationMinutes = 7,
                isRestDay = false,
                exerciseType = "abs",
                exerciseIds = listOf(
                    "abs_001",
                    "abs_005",
                    "abs_009",
                    "abs_010",
                    "abs_015",
                    "abs_017",
                    "abs_018"
                )
            ),
            PlanDay(
                dayNumber = 7,
                title = "Ngày 7",
                exerciseCount = 7,
                durationMinutes = 8,
                isRestDay = false,
                exerciseType = "abs",
                exerciseIds = listOf(
                    "abs_003",
                    "abs_004",
                    "abs_007",
                    "abs_011",
                    "abs_014",
                    "abs_019",
                    "abs_021"
                )
            ),
            PlanDay(
                dayNumber = 8,
                title = "Ngày 8",
                exerciseCount = 0,
                durationMinutes = 0,
                isRestDay = true,
                exerciseType = "abs",
                exerciseIds = emptyList()
            ),
            PlanDay(
                dayNumber = 9,
                title = "Ngày 9",
                exerciseCount = 8,
                durationMinutes = 8,
                isRestDay = false,
                exerciseType = "abs",
                exerciseIds = listOf(
                    "abs_001",
                    "abs_002",
                    "abs_005",
                    "abs_006",
                    "abs_009",
                    "abs_010",
                    "abs_015",
                    "abs_016"
                )
            ),
            PlanDay(
                dayNumber = 10,
                title = "Ngày 10",
                exerciseCount = 8,
                durationMinutes = 8,
                isRestDay = false,
                exerciseType = "abs",
                exerciseIds = listOf(
                    "abs_003",
                    "abs_004",
                    "abs_007",
                    "abs_008",
                    "abs_011",
                    "abs_012",
                    "abs_014",
                    "abs_019"
                )
            ),
            PlanDay(
                dayNumber = 11,
                title = "Ngày 11",
                exerciseCount = 8,
                durationMinutes = 9,
                isRestDay = false,
                exerciseType = "abs",
                exerciseIds = listOf(
                    "abs_001",
                    "abs_005",
                    "abs_011",
                    "abs_013",
                    "abs_015",
                    "abs_017",
                    "abs_018",
                    "abs_021"
                )
            ),
            PlanDay(
                dayNumber = 12,
                title = "Ngày 12",
                exerciseCount = 0,
                durationMinutes = 0,
                isRestDay = true,
                exerciseType = "abs",
                exerciseIds = emptyList()
            ),
            PlanDay(
                dayNumber = 13,
                title = "Ngày 13",
                exerciseCount = 8,
                durationMinutes = 9,
                isRestDay = false,
                exerciseType = "abs",
                exerciseIds = listOf(
                    "abs_002",
                    "abs_003",
                    "abs_004",
                    "abs_006",
                    "abs_008",
                    "abs_014",
                    "abs_020",
                    "abs_021"
                )
            ),
            PlanDay(
                dayNumber = 14,
                title = "Ngày 14",
                exerciseCount = 8,
                durationMinutes = 9,
                isRestDay = false,
                exerciseType = "abs",
                exerciseIds = listOf(
                    "abs_001",
                    "abs_005",
                    "abs_007",
                    "abs_009",
                    "abs_010",
                    "abs_011",
                    "abs_017",
                    "abs_018"
                )
            ),
            PlanDay(
                dayNumber = 15,
                title = "Ngày 15",
                exerciseCount = 8,
                durationMinutes = 10,
                isRestDay = false,
                exerciseType = "abs",
                exerciseIds = listOf(
                    "abs_003",
                    "abs_004",
                    "abs_012",
                    "abs_013",
                    "abs_014",
                    "abs_015",
                    "abs_019",
                    "abs_021"
                )
            ),
            PlanDay(
                dayNumber = 16,
                title = "Ngày 16",
                exerciseCount = 0,
                durationMinutes = 0,
                isRestDay = true,
                exerciseType = "abs",
                exerciseIds = emptyList()
            ),
            PlanDay(
                dayNumber = 17,
                title = "Ngày 17",
                exerciseCount = 9,
                durationMinutes = 10,
                isRestDay = false,
                exerciseType = "abs",
                exerciseIds = listOf(
                    "abs_001",
                    "abs_002",
                    "abs_005",
                    "abs_006",
                    "abs_009",
                    "abs_010",
                    "abs_015",
                    "abs_017",
                    "abs_018"
                )
            ),
            PlanDay(
                dayNumber = 18,
                title = "Ngày 18",
                exerciseCount = 9,
                durationMinutes = 10,
                isRestDay = false,
                exerciseType = "abs",
                exerciseIds = listOf(
                    "abs_003",
                    "abs_004",
                    "abs_007",
                    "abs_008",
                    "abs_011",
                    "abs_012",
                    "abs_013",
                    "abs_020",
                    "abs_021"
                )
            ),
            PlanDay(
                dayNumber = 19,
                title = "Ngày 19",
                exerciseCount = 9,
                durationMinutes = 11,
                isRestDay = false,
                exerciseType = "abs",
                exerciseIds = listOf(
                    "abs_001",
                    "abs_005",
                    "abs_009",
                    "abs_010",
                    "abs_014",
                    "abs_015",
                    "abs_016",
                    "abs_017",
                    "abs_018"
                )
            ),
            PlanDay(
                dayNumber = 20,
                title = "Ngày 20",
                exerciseCount = 0,
                durationMinutes = 0,
                isRestDay = true,
                exerciseType = "abs",
                exerciseIds = emptyList()
            ),
            PlanDay(
                dayNumber = 21,
                title = "Ngày 21",
                exerciseCount = 9,
                durationMinutes = 11,
                isRestDay = false,
                exerciseType = "abs",
                exerciseIds = listOf(
                    "abs_002",
                    "abs_003",
                    "abs_004",
                    "abs_006",
                    "abs_008",
                    "abs_012",
                    "abs_015",
                    "abs_020",
                    "abs_021"
                )
            ),
            PlanDay(
                dayNumber = 22,
                title = "Ngày 22",
                exerciseCount = 9,
                durationMinutes = 11,
                isRestDay = false,
                exerciseType = "abs",
                exerciseIds = listOf(
                    "abs_001",
                    "abs_005",
                    "abs_007",
                    "abs_009",
                    "abs_010",
                    "abs_011",
                    "abs_013",
                    "abs_017",
                    "abs_018"
                )
            ),
            PlanDay(
                dayNumber = 23,
                title = "Ngày 23",
                exerciseCount = 9,
                durationMinutes = 12,
                isRestDay = false,
                exerciseType = "abs",
                exerciseIds = listOf(
                    "abs_002",
                    "abs_004",
                    "abs_006",
                    "abs_014",
                    "abs_015",
                    "abs_017",
                    "abs_018",
                    "abs_020",
                    "abs_021"
                )
            ),
            PlanDay(
                dayNumber = 24,
                title = "Ngày 24",
                exerciseCount = 0,
                durationMinutes = 0,
                isRestDay = true,
                exerciseType = "abs",
                exerciseIds = emptyList()
            ),
            PlanDay(
                dayNumber = 25,
                title = "Ngày 25",
                exerciseCount = 10,
                durationMinutes = 12,
                isRestDay = false,
                exerciseType = "abs",
                exerciseIds = listOf(
                    "abs_001",
                    "abs_002",
                    "abs_003",
                    "abs_005",
                    "abs_007",
                    "abs_009",
                    "abs_010",
                    "abs_015",
                    "abs_017",
                    "abs_018"
                )
            ),
            PlanDay(
                dayNumber = 26,
                title = "Ngày 26",
                exerciseCount = 10,
                durationMinutes = 12,
                isRestDay = false,
                exerciseType = "abs",
                exerciseIds = listOf(
                    "abs_004",
                    "abs_006",
                    "abs_008",
                    "abs_011",
                    "abs_012",
                    "abs_013",
                    "abs_014",
                    "abs_016",
                    "abs_020",
                    "abs_021"
                )
            ),
            PlanDay(
                dayNumber = 27,
                title = "Ngày 27",
                exerciseCount = 10,
                durationMinutes = 13,
                isRestDay = false,
                exerciseType = "abs",
                exerciseIds = listOf(
                    "abs_001",
                    "abs_003",
                    "abs_005",
                    "abs_009",
                    "abs_010",
                    "abs_011",
                    "abs_014",
                    "abs_015",
                    "abs_017",
                    "abs_018"
                )
            ),
            PlanDay(
                dayNumber = 28,
                title = "Ngày 28",
                exerciseCount = 0,
                durationMinutes = 0,
                isRestDay = true,
                exerciseType = "abs",
                exerciseIds = emptyList()
            ),
            PlanDay(
                dayNumber = 29,
                title = "Ngày 29",
                exerciseCount = 10,
                durationMinutes = 13,
                isRestDay = false,
                exerciseType = "abs",
                exerciseIds = listOf(
                    "abs_002",
                    "abs_004",
                    "abs_006",
                    "abs_008",
                    "abs_009",
                    "abs_010",
                    "abs_012",
                    "abs_015",
                    "abs_020",
                    "abs_021"
                )
            ),
            PlanDay(
                dayNumber = 30,
                title = "Ngày 30",
                exerciseCount = 10,
                durationMinutes = 15,
                isRestDay = false,
                exerciseType = "abs",
                exerciseIds = listOf(
                    "abs_001",
                    "abs_003",
                    "abs_005",
                    "abs_007",
                    "abs_009",
                    "abs_010",
                    "abs_014",
                    "abs_015",
                    "abs_017",
                    "abs_018"
                )
            )
        )
    }
}