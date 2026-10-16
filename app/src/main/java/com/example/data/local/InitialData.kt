package com.example.data.local

import com.example.data.model.CompletionStatus
import com.example.data.model.DifficultyLevel
import com.example.data.model.EquipmentType
import com.example.data.model.ExerciseCategory
import com.example.data.model.FitnessGoal
import com.example.data.model.MuscleGroup
import com.example.data.model.QuestPeriod

object InitialData {

    fun getDefaultUserProfile(): UserProfileEntity {
        return UserProfileEntity(
            id = "default_user",
            displayName = "Adventurer",
            ageRange = "25-34",
            experienceLevel = DifficultyLevel.BEGINNER.name,
            primaryGoal = FitnessGoal.CONSISTENCY.name,
            preferredTrainingDays = "1,3,5", // Mon, Wed, Fri
            preferredDurationMinutes = 30,
            hasBarbell = true,
            hasDumbbells = true,
            hasBodyweight = true,
            avoidedMovements = "",
            heightCm = 175.0,
            weightKg = 72.0,
            preferredWeightUnit = "kg",
            hasCompletedOnboarding = false,
            notificationsEnabled = true,
            reminderHour = 18,
            reminderMinute = 0,
            currentStreak = 0,
            longestStreak = 0,
            lastWorkoutDateIso = "",
            totalXp = 0,
            currentLevel = 1,
            weeklyTargetWorkouts = 3,
            dailyTargetMinutes = 30
        )
    }

    fun getDefaultExercises(): List<ExerciseEntity> {
        return listOf(
            ExerciseEntity(
                id = "bw_squat",
                name = "Bodyweight Squat",
                description = "Fundamental lower body movement strengthening quads, glutes, and core stability without equipment.",
                primaryMuscle = MuscleGroup.LEGS.name,
                secondaryMuscles = "${MuscleGroup.CORE.name},${MuscleGroup.HAMSTRINGS.name}",
                equipment = EquipmentType.BODYWEIGHT.name,
                category = ExerciseCategory.LOWER_BODY.name,
                difficulty = DifficultyLevel.BEGINNER.name,
                instructions = "Stand feet shoulder-width apart|Brace your core and sit back into your hips as if sitting in a chair|Lower until thighs are parallel to floor while keeping chest tall|Drive through heels to stand back up",
                commonMistakes = "Knees caving inward|Heels lifting off the ground|Rounding the lower back",
                easierVariation = "Box Squat onto a sturdy chair",
                progression = "Dumbbell Goblet Squat or Pause Squat",
                defaultSets = 3,
                defaultReps = 12,
                defaultWeightKg = 0.0,
                defaultRestSeconds = 60
            ),
            ExerciseEntity(
                id = "bw_pushup",
                name = "Push-Up",
                description = "Classic upper body compound exercise developing chest, front deltoids, and triceps with bodyweight.",
                primaryMuscle = MuscleGroup.CHEST.name,
                secondaryMuscles = "${MuscleGroup.TRICEPS.name},${MuscleGroup.SHOULDERS.name},${MuscleGroup.CORE.name}",
                equipment = EquipmentType.BODYWEIGHT.name,
                category = ExerciseCategory.UPPER_BODY.name,
                difficulty = DifficultyLevel.BEGINNER.name,
                instructions = "Place hands slightly wider than shoulder-width on the floor|Keep body in a rigid straight line from head to heels|Lower chest down until elbows reach 90 degrees or chest hovers above floor|Press the floor away to return to top plank",
                commonMistakes = "Hips sagging down|Elbows flaring out perpendicular to torso|Craning neck toward floor",
                easierVariation = "Knee Push-Ups or Incline Hands-Elevated Push-Ups",
                progression = "Decline Push-Ups or Slow 3-Second Eccentric Push-Ups",
                defaultSets = 3,
                defaultReps = 10,
                defaultWeightKg = 0.0,
                defaultRestSeconds = 60
            ),
            ExerciseEntity(
                id = "bw_knee_pushup",
                name = "Knee Push-Up",
                description = "Gentle pushing variation reducing load while building chest and arm strength safely.",
                primaryMuscle = MuscleGroup.CHEST.name,
                secondaryMuscles = "${MuscleGroup.TRICEPS.name},${MuscleGroup.SHOULDERS.name}",
                equipment = EquipmentType.BODYWEIGHT.name,
                category = ExerciseCategory.UPPER_BODY.name,
                difficulty = DifficultyLevel.BEGINNER.name,
                instructions = "Kneel on floor with knees hip-width and hands under shoulders|Maintain straight torso from knees to head|Lower chest smoothly|Press firmly back up",
                commonMistakes = "Sticking butt back instead of hinging from knees|Dropping head",
                easierVariation = "Wall Push-Up",
                progression = "Standard Full Push-Up",
                defaultSets = 3,
                defaultReps = 10,
                defaultWeightKg = 0.0,
                defaultRestSeconds = 60
            ),
            ExerciseEntity(
                id = "bw_reverse_lunge",
                name = "Reverse Lunge",
                description = "Unilateral leg exercise that develops knee stability, quad power, and glute strength.",
                primaryMuscle = MuscleGroup.LEGS.name,
                secondaryMuscles = "${MuscleGroup.HAMSTRINGS.name},${MuscleGroup.CORE.name}",
                equipment = EquipmentType.BODYWEIGHT.name,
                category = ExerciseCategory.LOWER_BODY.name,
                difficulty = DifficultyLevel.BEGINNER.name,
                instructions = "Stand tall with feet together|Step one foot back and lower both knees to 90-degree angles|Keep front knee tracked over ankle|Push through front heel to return to standing",
                commonMistakes = "Front knee slamming past toes|Leaning torso excessively forward|Banging back knee on floor",
                easierVariation = "Supported Static Split Squat holding a wall",
                progression = "Dumbbell Reverse Lunge",
                defaultSets = 3,
                defaultReps = 10,
                defaultWeightKg = 0.0,
                defaultRestSeconds = 60
            ),
            ExerciseEntity(
                id = "bw_glute_bridge",
                name = "Glute Bridge",
                description = "Floor exercise targeting the glutes and lower back safely without spinal compression.",
                primaryMuscle = MuscleGroup.HAMSTRINGS.name,
                secondaryMuscles = "${MuscleGroup.CORE.name},${MuscleGroup.LEGS.name}",
                equipment = EquipmentType.BODYWEIGHT.name,
                category = ExerciseCategory.LOWER_BODY.name,
                difficulty = DifficultyLevel.BEGINNER.name,
                instructions = "Lie on back with knees bent and feet flat on floor hip-width apart|Engage core and squeeze glutes to lift hips toward ceiling|Form a straight diagonal line from knees to shoulders|Hold 1 second at top and lower under control",
                commonMistakes = "Hyperextending lower back at top|Pushing through toes instead of heels",
                easierVariation = "Glute bridge with hands supporting pelvis",
                progression = "Single-Leg Glute Bridge or Dumbbell Hip Bridge",
                defaultSets = 3,
                defaultReps = 12,
                defaultWeightKg = 0.0,
                defaultRestSeconds = 45
            ),
            ExerciseEntity(
                id = "bw_plank",
                name = "Forearm Plank",
                description = "Static core isometric building deep transverse abdominis resilience and posture.",
                primaryMuscle = MuscleGroup.CORE.name,
                secondaryMuscles = "${MuscleGroup.SHOULDERS.name},${MuscleGroup.LEGS.name}",
                equipment = EquipmentType.BODYWEIGHT.name,
                category = ExerciseCategory.CORE.name,
                difficulty = DifficultyLevel.BEGINNER.name,
                instructions = "Rest on forearms and toes with elbows directly under shoulders|Keep body in a rigid plank line|Brace abdominals firmly as if bracing for a punch|Breathe steadily without letting hips sag",
                commonMistakes = "Hips drooping toward floor|Hiking hips up into a pike|Holding breath",
                easierVariation = "Kneeling Plank",
                progression = "Plank with Alternating Shoulder Taps",
                defaultSets = 3,
                defaultReps = 1,
                defaultWeightKg = 0.0,
                defaultRestSeconds = 45,
                isTimed = true,
                defaultDurationSeconds = 30
            ),
            ExerciseEntity(
                id = "bw_bicycle_crunches",
                name = "Bicycle Crunches",
                description = "Dynamic rotational core movement activating obliques and rectus abdominis.",
                primaryMuscle = MuscleGroup.CORE.name,
                secondaryMuscles = "${MuscleGroup.LEGS.name}",
                equipment = EquipmentType.BODYWEIGHT.name,
                category = ExerciseCategory.CORE.name,
                difficulty = DifficultyLevel.INTERMEDIATE.name,
                instructions = "Lie on back with fingertips behind ears and knees at 90 degrees|Bring right elbow toward left knee while extending right leg|Switch smoothly and bring left elbow toward right knee|Maintain steady rhythmic tempo",
                commonMistakes = "Yanking neck with hands|Moving too quickly without controlled contraction",
                easierVariation = "Dead Bug or Standard Crunch",
                progression = "Bicycle Crunch with 2-second pause at rotation",
                defaultSets = 3,
                defaultReps = 16,
                defaultWeightKg = 0.0,
                defaultRestSeconds = 45
            ),
            ExerciseEntity(
                id = "bw_mountain_climber",
                name = "Mountain Climber",
                description = "High-energy core and shoulder endurance movement performed in a tall plank.",
                primaryMuscle = MuscleGroup.CORE.name,
                secondaryMuscles = "${MuscleGroup.SHOULDERS.name},${MuscleGroup.LEGS.name}",
                equipment = EquipmentType.BODYWEIGHT.name,
                category = ExerciseCategory.SHORT_CIRCUIT.name,
                difficulty = DifficultyLevel.BEGINNER.name,
                instructions = "Start in push-up plank position with hands under shoulders|Drive right knee forward toward chest without letting hips bounce|Quickly switch legs driving left knee forward|Keep upper body stable",
                commonMistakes = "Bouncing hips high in air|Letting shoulders drift behind wrists",
                easierVariation = "Slow Elevated Mountain Climbers",
                progression = "Cross-Body Mountain Climbers",
                defaultSets = 3,
                defaultReps = 20,
                defaultWeightKg = 0.0,
                defaultRestSeconds = 45
            ),
            ExerciseEntity(
                id = "db_goblet_squat",
                name = "Dumbbell Goblet Squat",
                description = "Excellent loaded squat pattern holding one dumbbell vertically at the chest.",
                primaryMuscle = MuscleGroup.LEGS.name,
                secondaryMuscles = "${MuscleGroup.CORE.name},${MuscleGroup.HAMSTRINGS.name}",
                equipment = EquipmentType.DUMBBELLS.name,
                category = ExerciseCategory.LOWER_BODY.name,
                difficulty = DifficultyLevel.INTERMEDIATE.name,
                instructions = "Hold a dumbbell vertically against chest with both hands cradling top bell|Feet slightly wider than shoulders, toes angled out slightly|Squat down deeply while keeping elbows tracking inside knees|Push through floor to return to standing",
                commonMistakes = "Letting weight pull chest forward|Collapsing knees inwards",
                easierVariation = "Bodyweight Squat",
                progression = "Heavier Dumbbell Goblet Squat or 3-Second Tempo Squat",
                defaultSets = 3,
                defaultReps = 10,
                defaultWeightKg = 10.0,
                defaultRestSeconds = 60
            ),
            ExerciseEntity(
                id = "db_floor_press",
                name = "Dumbbell Floor Press",
                description = "Safe home horizontal press performed lying on the floor without requiring a gym bench.",
                primaryMuscle = MuscleGroup.CHEST.name,
                secondaryMuscles = "${MuscleGroup.TRICEPS.name},${MuscleGroup.SHOULDERS.name}",
                equipment = EquipmentType.DUMBBELLS.name,
                category = ExerciseCategory.UPPER_BODY.name,
                difficulty = DifficultyLevel.BEGINNER.name,
                instructions = "Lie on back on the floor with knees bent and feet flat|Hold a dumbbell in each hand over chest with arms extended|Lower weights slowly until upper arms gently touch the floor|Pause briefly and press weights smoothly back to ceiling",
                commonMistakes = "Banging elbows hard onto floor|Bouncing elbows off floor",
                easierVariation = "Push-Up or Knee Push-Up",
                progression = "Dumbbell Floor Press with single-arm focus",
                defaultSets = 3,
                defaultReps = 10,
                defaultWeightKg = 12.0,
                defaultRestSeconds = 60
            ),
            ExerciseEntity(
                id = "db_bent_over_row",
                name = "Dumbbell Bent-Over Row",
                description = "Upper back builder developing lats, rhomboids, and rear delts with dual dumbbells.",
                primaryMuscle = MuscleGroup.BACK.name,
                secondaryMuscles = "${MuscleGroup.BICEPS.name},${MuscleGroup.SHOULDERS.name},${MuscleGroup.CORE.name}",
                equipment = EquipmentType.DUMBBELLS.name,
                category = ExerciseCategory.UPPER_BODY.name,
                difficulty = DifficultyLevel.BEGINNER.name,
                instructions = "Hold a dumbbell in each hand, hinge hips back with slight knee bend and flat back|Let arms hang naturally below shoulders|Pull dumbbells up toward ribs leading with elbows|Squeeze shoulder blades together at top, then lower smoothly",
                commonMistakes = "Rounding the lower back|Using body jerk to heave weights up",
                easierVariation = "Single-arm Dumbbell Row supported on knee",
                progression = "Barbell Bent-Over Row",
                defaultSets = 3,
                defaultReps = 10,
                defaultWeightKg = 10.0,
                defaultRestSeconds = 60
            ),
            ExerciseEntity(
                id = "db_overhead_press",
                name = "Dumbbell Overhead Press",
                description = "Standing shoulder exercise developing deltoids, triceps, and upright posture.",
                primaryMuscle = MuscleGroup.SHOULDERS.name,
                secondaryMuscles = "${MuscleGroup.TRICEPS.name},${MuscleGroup.CORE.name}",
                equipment = EquipmentType.DUMBBELLS.name,
                category = ExerciseCategory.UPPER_BODY.name,
                difficulty = DifficultyLevel.INTERMEDIATE.name,
                instructions = "Stand feet hip-width apart holding dumbbells at shoulder level with palms facing forward or neutral|Brace glutes and core to protect spine|Press dumbbells smoothly overhead until arms are extended|Lower slowly under control to shoulder level",
                commonMistakes = "Arching lower back excessively|Flaring ribs upward",
                easierVariation = "Seated Floor Dumbbell Press",
                progression = "Barbell Standing Overhead Press",
                defaultSets = 3,
                defaultReps = 8,
                defaultWeightKg = 8.0,
                defaultRestSeconds = 60
            ),
            ExerciseEntity(
                id = "db_romanian_deadlift",
                name = "Dumbbell Romanian Deadlift",
                description = "Posterior chain movement loading hamstrings and glutes through a hip hinge.",
                primaryMuscle = MuscleGroup.HAMSTRINGS.name,
                secondaryMuscles = "${MuscleGroup.BACK.name},${MuscleGroup.CORE.name}",
                equipment = EquipmentType.DUMBBELLS.name,
                category = ExerciseCategory.LOWER_BODY.name,
                difficulty = DifficultyLevel.INTERMEDIATE.name,
                instructions = "Stand tall holding dumbbells in front of thighs|Soft bend in knees, push hips backward while sliding dumbbells down shins|Keep spine neutral and shoulder blades set|Drive hips forward to stand tall once hamstrings stretch",
                commonMistakes = "Squatting down instead of hinging hips back|Curving spine",
                easierVariation = "Bodyweight Good Mornings",
                progression = "Barbell Romanian Deadlift",
                defaultSets = 3,
                defaultReps = 10,
                defaultWeightKg = 12.0,
                defaultRestSeconds = 60
            ),
            ExerciseEntity(
                id = "db_bicep_curl",
                name = "Dumbbell Bicep Curl",
                description = "Arm exercise isolating biceps and forearms with controlled elbow flexion.",
                primaryMuscle = MuscleGroup.BICEPS.name,
                secondaryMuscles = "${MuscleGroup.CHEST.name}",
                equipment = EquipmentType.DUMBBELLS.name,
                category = ExerciseCategory.UPPER_BODY.name,
                difficulty = DifficultyLevel.BEGINNER.name,
                instructions = "Stand with dumbbells at sides, palms facing inward|Curl weights upward while rotating palms to face shoulders|Squeeze biceps at top without swinging elbows forward|Lower slowly to full extension",
                commonMistakes = "Swinging torso to build momentum|Letting elbows drift backward",
                easierVariation = "Alternating Light Dumbbell Curls",
                progression = "Dumbbell Hammer Curls or Incline Tempo Curls",
                defaultSets = 3,
                defaultReps = 12,
                defaultWeightKg = 8.0,
                defaultRestSeconds = 45
            ),
            ExerciseEntity(
                id = "db_lateral_raise",
                name = "Dumbbell Lateral Raise",
                description = "Shoulder isolation targeting lateral deltoid heads for shoulder width.",
                primaryMuscle = MuscleGroup.SHOULDERS.name,
                secondaryMuscles = "${MuscleGroup.BACK.name}",
                equipment = EquipmentType.DUMBBELLS.name,
                category = ExerciseCategory.UPPER_BODY.name,
                difficulty = DifficultyLevel.BEGINNER.name,
                instructions = "Hold light dumbbells at sides with a slight elbow bend|Raise arms out to sides until parallel to floor leading with elbows|Lower slowly resisting gravity",
                commonMistakes = "Using heavy weight and shrugging neck|Swinging torso",
                easierVariation = "Thumbs-up Scaption Raise with lighter weight",
                progression = "Pause Lateral Raise at top for 2 seconds",
                defaultSets = 3,
                defaultReps = 12,
                defaultWeightKg = 5.0,
                defaultRestSeconds = 45
            ),
            ExerciseEntity(
                id = "bb_floor_press",
                name = "Barbell Floor Press",
                description = "Heavy home compound press lying on floor, eliminating shoulder strain while building pressing power.",
                primaryMuscle = MuscleGroup.CHEST.name,
                secondaryMuscles = "${MuscleGroup.TRICEPS.name},${MuscleGroup.SHOULDERS.name}",
                equipment = EquipmentType.BARBELL.name,
                category = ExerciseCategory.STRENGTH.name,
                difficulty = DifficultyLevel.INTERMEDIATE.name,
                instructions = "Lie flat on floor under barbell with knees bent|Grip bar evenly just outside shoulder width|Lower bar carefully until upper arms rest on floor|Press firmly back up to full arm extension",
                commonMistakes = "Dropping bar down onto ribcage|Uneven grip",
                easierVariation = "Dumbbell Floor Press",
                progression = "Barbell Floor Press with progressive plate loading",
                defaultSets = 3,
                defaultReps = 8,
                defaultWeightKg = 30.0,
                defaultRestSeconds = 90
            ),
            ExerciseEntity(
                id = "bb_bent_over_row",
                name = "Barbell Bent-Over Row",
                description = "Premier back strength builder targeting lats, upper back, and grip strength.",
                primaryMuscle = MuscleGroup.BACK.name,
                secondaryMuscles = "${MuscleGroup.BICEPS.name},${MuscleGroup.CORE.name},${MuscleGroup.HAMSTRINGS.name}",
                equipment = EquipmentType.BARBELL.name,
                category = ExerciseCategory.STRENGTH.name,
                difficulty = DifficultyLevel.INTERMEDIATE.name,
                instructions = "Stand over barbell, hinge hips back to 45-degree torso angle with flat back|Grip bar slightly wider than shoulder width|Pull bar up toward lower ribcage squeezing lats|Lower bar slowly back below knees",
                commonMistakes = "Standing upright instead of holding hip hinge|Rounding lower back",
                easierVariation = "Dumbbell Bent-Over Row",
                progression = "Pendlay Row from floor",
                defaultSets = 3,
                defaultReps = 8,
                defaultWeightKg = 30.0,
                defaultRestSeconds = 90
            ),
            ExerciseEntity(
                id = "bb_romanian_deadlift",
                name = "Barbell Romanian Deadlift",
                description = "Essential barbell hip hinge strengthening glutes, hamstrings, and erectors safely.",
                primaryMuscle = MuscleGroup.HAMSTRINGS.name,
                secondaryMuscles = "${MuscleGroup.LEGS.name},${MuscleGroup.BACK.name},${MuscleGroup.CORE.name}",
                equipment = EquipmentType.BARBELL.name,
                category = ExerciseCategory.STRENGTH.name,
                difficulty = DifficultyLevel.INTERMEDIATE.name,
                instructions = "Deadlift bar to standing position|Unlock knees slightly, then push hips back as far as possible|Slide barbell down thighs to mid-shin level keeping back neutral|Squeeze glutes to return to standing",
                commonMistakes = "Letting barbell drift away from legs|Rounding spine",
                easierVariation = "Dumbbell Romanian Deadlift",
                progression = "Deficit Barbell RDL",
                defaultSets = 3,
                defaultReps = 8,
                defaultWeightKg = 35.0,
                defaultRestSeconds = 90
            ),
            ExerciseEntity(
                id = "bb_overhead_press",
                name = "Barbell Overhead Press",
                description = "True vertical press test developing upper-body pressing strength and core stability.",
                primaryMuscle = MuscleGroup.SHOULDERS.name,
                secondaryMuscles = "${MuscleGroup.TRICEPS.name},${MuscleGroup.CORE.name}",
                equipment = EquipmentType.BARBELL.name,
                category = ExerciseCategory.STRENGTH.name,
                difficulty = DifficultyLevel.ADVANCED.name,
                instructions = "Clean bar to front shoulders with elbows slightly in front of bar|Brace glutes, quads, and abs tight|Press bar upward in a straight line, pulling chin back slightly to clear path|Lock out directly over crown of head",
                commonMistakes = "Leaning back excessively|Pressing forward rather than vertically",
                easierVariation = "Dumbbell Overhead Press",
                progression = "Strict Barbell Press with increased weight",
                defaultSets = 3,
                defaultReps = 6,
                defaultWeightKg = 25.0,
                defaultRestSeconds = 90
            ),
            ExerciseEntity(
                id = "mob_flow",
                name = "Dynamic Full-Body Mobility Flow",
                description = "Fluid series of bodyweight stretches opening hips, thoracic spine, and hamstrings.",
                primaryMuscle = MuscleGroup.MOBILITY.name,
                secondaryMuscles = "${MuscleGroup.LEGS.name},${MuscleGroup.BACK.name}",
                equipment = EquipmentType.BODYWEIGHT.name,
                category = ExerciseCategory.MOBILITY.name,
                difficulty = DifficultyLevel.BEGINNER.name,
                instructions = "Step into a deep lunge, place hands on floor inside front foot|Reach one hand toward ceiling rotating upper spine|Hold 3 seconds and return to plank|Switch sides and repeat smoothly",
                commonMistakes = "Rushing through positions|Holding breath during twists",
                easierVariation = "Kneeling Thoracic Rotations",
                progression = "World's Greatest Stretch with Hamstring Stretch",
                defaultSets = 2,
                defaultReps = 1,
                defaultWeightKg = 0.0,
                defaultRestSeconds = 30,
                isTimed = true,
                defaultDurationSeconds = 60
            ),
            ExerciseEntity(
                id = "bw_dead_bug",
                name = "Dead Bug",
                description = "Low-back-friendly anti-extension core exercise establishing motor control.",
                primaryMuscle = MuscleGroup.CORE.name,
                secondaryMuscles = "${MuscleGroup.SHOULDERS.name}",
                equipment = EquipmentType.BODYWEIGHT.name,
                category = ExerciseCategory.CORE.name,
                difficulty = DifficultyLevel.BEGINNER.name,
                instructions = "Lie on back with arms pointing to ceiling and knees bent at 90 degrees|Press lower back firmly into the floor|Simultaneously lower right arm overhead and left leg toward floor|Return to center and switch opposite limbs",
                commonMistakes = "Arching lower back off the floor|Moving too quickly",
                easierVariation = "Dead bug tapping heels without moving arms",
                progression = "Dead bug with light dumbbell in hands",
                defaultSets = 3,
                defaultReps = 12,
                defaultWeightKg = 0.0,
                defaultRestSeconds = 45
            )
        )
    }

    fun getDefaultTemplates(): List<WorkoutTemplateEntity> {
        return listOf(
            WorkoutTemplateEntity(
                id = "template_full_body_quest_1",
                title = "Full Body Quest I",
                subtitle = "Balanced strength & conditioning foundation",
                description = "Engaging full-body routine balancing squats, presses, rows, and core stability for total fitness progress.",
                category = ExerciseCategory.FULL_BODY.name,
                difficulty = DifficultyLevel.BEGINNER.name,
                estimatedDurationMinutes = 30,
                isCustom = false,
                isFavorite = true,
                xpReward = 50
            ),
            WorkoutTemplateEntity(
                id = "template_upper_body_conqueror",
                title = "Upper Body Conqueror",
                subtitle = "Chest, back, and arm power",
                description = "Focused upper body session using dual dumbbells and bodyweight to forge upper torso strength.",
                category = ExerciseCategory.UPPER_BODY.name,
                difficulty = DifficultyLevel.INTERMEDIATE.name,
                estimatedDurationMinutes = 35,
                isCustom = false,
                isFavorite = false,
                xpReward = 50
            ),
            WorkoutTemplateEntity(
                id = "template_lower_titan",
                title = "Lower Body & Core Titan",
                subtitle = "Leg drive and posterior chain stability",
                description = "Develop resilient legs, strong hamstrings, and a solid core using dumbbells and bodyweight.",
                category = ExerciseCategory.LOWER_BODY.name,
                difficulty = DifficultyLevel.INTERMEDIATE.name,
                estimatedDurationMinutes = 30,
                isCustom = false,
                isFavorite = false,
                xpReward = 50
            ),
            WorkoutTemplateEntity(
                id = "template_bodyweight_hero",
                title = "Bodyweight Hero Circuit",
                subtitle = "Zero-equipment anytime conditioning",
                description = "Pure bodyweight session you can complete anywhere in your home with no gear required.",
                category = ExerciseCategory.BODYWEIGHT.name,
                difficulty = DifficultyLevel.BEGINNER.name,
                estimatedDurationMinutes = 25,
                isCustom = false,
                isFavorite = true,
                xpReward = 50
            ),
            WorkoutTemplateEntity(
                id = "template_barbell_odyssey",
                title = "Barbell Strength Odyssey",
                subtitle = "Fundamental home barbell compound lifts",
                description = "Harness your barbell for classic floor pressing, bent-over rows, and Romanian deadlifts.",
                category = ExerciseCategory.STRENGTH.name,
                difficulty = DifficultyLevel.INTERMEDIATE.name,
                estimatedDurationMinutes = 40,
                isCustom = false,
                isFavorite = false,
                xpReward = 60
            ),
            WorkoutTemplateEntity(
                id = "template_quick_15_ignite",
                title = "Quick 15-Min Ignite",
                subtitle = "Time-crunched high-efficiency burn",
                description = "Fast, energizing routine perfect for busy days to keep your workout streak intact.",
                category = ExerciseCategory.SHORT_CIRCUIT.name,
                difficulty = DifficultyLevel.BEGINNER.name,
                estimatedDurationMinutes = 15,
                isCustom = false,
                isFavorite = false,
                xpReward = 40
            ),
            WorkoutTemplateEntity(
                id = "template_mobility_sanctuary",
                title = "Mobility & Recovery Sanctuary",
                subtitle = "Gentle joint health & movement prep",
                description = "Active recovery routine designed for rest days to refresh joints and relieve tight muscles.",
                category = ExerciseCategory.MOBILITY.name,
                difficulty = DifficultyLevel.BEGINNER.name,
                estimatedDurationMinutes = 20,
                isCustom = false,
                isFavorite = false,
                xpReward = 35
            )
        )
    }

    fun getDefaultTemplateExercises(): List<TemplateExerciseEntity> {
        val list = mutableListOf<TemplateExerciseEntity>()

        // 1. Full Body Quest 1
        list.add(TemplateExerciseEntity("template_full_body_quest_1", "db_goblet_squat", "Dumbbell Goblet Squat", EquipmentType.DUMBBELLS.name, 3, 10, 10.0, 60, false, 0, 0))
        list.add(TemplateExerciseEntity("template_full_body_quest_1", "bw_pushup", "Push-Up", EquipmentType.BODYWEIGHT.name, 3, 10, 0.0, 60, false, 0, 1))
        list.add(TemplateExerciseEntity("template_full_body_quest_1", "db_bent_over_row", "Dumbbell Bent-Over Row", EquipmentType.DUMBBELLS.name, 3, 10, 10.0, 60, false, 0, 2))
        list.add(TemplateExerciseEntity("template_full_body_quest_1", "db_romanian_deadlift", "Dumbbell Romanian Deadlift", EquipmentType.DUMBBELLS.name, 3, 10, 12.0, 60, false, 0, 3))
        list.add(TemplateExerciseEntity("template_full_body_quest_1", "bw_plank", "Forearm Plank", EquipmentType.BODYWEIGHT.name, 3, 1, 0.0, 45, true, 30, 4))

        // 2. Upper Body Conqueror
        list.add(TemplateExerciseEntity("template_upper_body_conqueror", "db_floor_press", "Dumbbell Floor Press", EquipmentType.DUMBBELLS.name, 3, 10, 12.0, 60, false, 0, 0))
        list.add(TemplateExerciseEntity("template_upper_body_conqueror", "db_bent_over_row", "Dumbbell Bent-Over Row", EquipmentType.DUMBBELLS.name, 3, 10, 10.0, 60, false, 0, 1))
        list.add(TemplateExerciseEntity("template_upper_body_conqueror", "db_overhead_press", "Dumbbell Overhead Press", EquipmentType.DUMBBELLS.name, 3, 8, 8.0, 60, false, 0, 2))
        list.add(TemplateExerciseEntity("template_upper_body_conqueror", "db_bicep_curl", "Dumbbell Bicep Curl", EquipmentType.DUMBBELLS.name, 3, 12, 8.0, 45, false, 0, 3))
        list.add(TemplateExerciseEntity("template_upper_body_conqueror", "bw_pushup", "Push-Up", EquipmentType.BODYWEIGHT.name, 3, 8, 0.0, 60, false, 0, 4))

        // 3. Lower Body & Core Titan
        list.add(TemplateExerciseEntity("template_lower_titan", "db_romanian_deadlift", "Dumbbell Romanian Deadlift", EquipmentType.DUMBBELLS.name, 3, 10, 12.0, 60, false, 0, 0))
        list.add(TemplateExerciseEntity("template_lower_titan", "bw_reverse_lunge", "Reverse Lunge", EquipmentType.BODYWEIGHT.name, 3, 10, 0.0, 60, false, 0, 1))
        list.add(TemplateExerciseEntity("template_lower_titan", "bw_glute_bridge", "Glute Bridge", EquipmentType.BODYWEIGHT.name, 3, 12, 0.0, 45, false, 0, 2))
        list.add(TemplateExerciseEntity("template_lower_titan", "bw_bicycle_crunches", "Bicycle Crunches", EquipmentType.BODYWEIGHT.name, 3, 16, 0.0, 45, false, 0, 3))
        list.add(TemplateExerciseEntity("template_lower_titan", "bw_plank", "Forearm Plank", EquipmentType.BODYWEIGHT.name, 3, 1, 0.0, 45, true, 30, 4))

        // 4. Bodyweight Hero Circuit
        list.add(TemplateExerciseEntity("template_bodyweight_hero", "bw_squat", "Bodyweight Squat", EquipmentType.BODYWEIGHT.name, 3, 12, 0.0, 45, false, 0, 0))
        list.add(TemplateExerciseEntity("template_bodyweight_hero", "bw_pushup", "Push-Up", EquipmentType.BODYWEIGHT.name, 3, 10, 0.0, 45, false, 0, 1))
        list.add(TemplateExerciseEntity("template_bodyweight_hero", "bw_reverse_lunge", "Reverse Lunge", EquipmentType.BODYWEIGHT.name, 3, 10, 0.0, 45, false, 0, 2))
        list.add(TemplateExerciseEntity("template_bodyweight_hero", "bw_glute_bridge", "Glute Bridge", EquipmentType.BODYWEIGHT.name, 3, 12, 0.0, 45, false, 0, 3))
        list.add(TemplateExerciseEntity("template_bodyweight_hero", "bw_plank", "Forearm Plank", EquipmentType.BODYWEIGHT.name, 3, 1, 0.0, 45, true, 30, 4))

        // 5. Barbell Strength Odyssey
        list.add(TemplateExerciseEntity("template_barbell_odyssey", "bb_romanian_deadlift", "Barbell Romanian Deadlift", EquipmentType.BARBELL.name, 3, 8, 35.0, 90, false, 0, 0))
        list.add(TemplateExerciseEntity("template_barbell_odyssey", "bb_floor_press", "Barbell Floor Press", EquipmentType.BARBELL.name, 3, 8, 30.0, 90, false, 0, 1))
        list.add(TemplateExerciseEntity("template_barbell_odyssey", "bb_bent_over_row", "Barbell Bent-Over Row", EquipmentType.BARBELL.name, 3, 8, 30.0, 90, false, 0, 2))
        list.add(TemplateExerciseEntity("template_barbell_odyssey", "bb_overhead_press", "Barbell Overhead Press", EquipmentType.BARBELL.name, 3, 6, 25.0, 90, false, 0, 3))

        // 6. Quick 15-Minute Ignite
        list.add(TemplateExerciseEntity("template_quick_15_ignite", "bw_squat", "Bodyweight Squat", EquipmentType.BODYWEIGHT.name, 2, 12, 0.0, 30, false, 0, 0))
        list.add(TemplateExerciseEntity("template_quick_15_ignite", "bw_pushup", "Push-Up", EquipmentType.BODYWEIGHT.name, 2, 8, 0.0, 30, false, 0, 1))
        list.add(TemplateExerciseEntity("template_quick_15_ignite", "bw_mountain_climber", "Mountain Climber", EquipmentType.BODYWEIGHT.name, 2, 16, 0.0, 30, false, 0, 2))
        list.add(TemplateExerciseEntity("template_quick_15_ignite", "bw_plank", "Forearm Plank", EquipmentType.BODYWEIGHT.name, 2, 1, 0.0, 30, true, 25, 3))

        // 7. Mobility Sanctuary
        list.add(TemplateExerciseEntity("template_mobility_sanctuary", "mob_flow", "Dynamic Full-Body Mobility Flow", EquipmentType.BODYWEIGHT.name, 2, 1, 0.0, 30, true, 60, 0))
        list.add(TemplateExerciseEntity("template_mobility_sanctuary", "bw_glute_bridge", "Glute Bridge", EquipmentType.BODYWEIGHT.name, 2, 10, 0.0, 30, false, 0, 1))
        list.add(TemplateExerciseEntity("template_mobility_sanctuary", "bw_dead_bug", "Dead Bug", EquipmentType.BODYWEIGHT.name, 2, 10, 0.0, 30, false, 0, 2))

        return list
    }

    fun getDefaultAchievements(): List<AchievementEntity> {
        return listOf(
            AchievementEntity(
                id = "first_workout",
                title = "Iron Initiate",
                description = "Complete your very first workout session in RepQuest.",
                badgeIconName = "military_tech",
                xpReward = 50,
                isUnlocked = false,
                currentProgress = 0,
                targetProgress = 1
            ),
            AchievementEntity(
                id = "five_workouts",
                title = "Pathfinder",
                description = "Log 5 completed workouts. Your discipline begins to shine.",
                badgeIconName = "explore",
                xpReward = 75,
                isUnlocked = false,
                currentProgress = 0,
                targetProgress = 5
            ),
            AchievementEntity(
                id = "ten_workouts",
                title = "Dungeon Delver",
                description = "Complete 10 workouts. True adventurers forge steady habits.",
                badgeIconName = "shield",
                xpReward = 100,
                isUnlocked = false,
                currentProgress = 0,
                targetProgress = 10
            ),
            AchievementEntity(
                id = "week_consistency",
                title = "Weekly Warrior",
                description = "Hit your weekly target number of workouts.",
                badgeIconName = "stars",
                xpReward = 75,
                isUnlocked = false,
                currentProgress = 0,
                targetProgress = 1
            ),
            AchievementEntity(
                id = "seven_days",
                title = "Relentless",
                description = "Log workouts across 7 distinct calendar days.",
                badgeIconName = "local_fire_department",
                xpReward = 120,
                isUnlocked = false,
                currentProgress = 0,
                targetProgress = 7
            ),
            AchievementEntity(
                id = "weekly_goal",
                title = "Goal Crusher",
                description = "Complete all planned training days in a single week.",
                badgeIconName = "emoji_events",
                xpReward = 80,
                isUnlocked = false,
                currentProgress = 0,
                targetProgress = 1
            ),
            AchievementEntity(
                id = "custom_workout",
                title = "Architect",
                description = "Create and save your first custom workout routine.",
                badgeIconName = "construction",
                xpReward = 40,
                isUnlocked = false,
                currentProgress = 0,
                targetProgress = 1
            ),
            AchievementEntity(
                id = "first_pr",
                title = "Record Breaker",
                description = "Set your first personal record in weight or repetitions.",
                badgeIconName = "trending_up",
                xpReward = 50,
                isUnlocked = false,
                currentProgress = 0,
                targetProgress = 1
            ),
            AchievementEntity(
                id = "ten_sets",
                title = "Set Champion",
                description = "Log at least 10 completed sets across your training history.",
                badgeIconName = "fitness_center",
                xpReward = 40,
                isUnlocked = false,
                currentProgress = 0,
                targetProgress = 10
            ),
            AchievementEntity(
                id = "consistency_champ",
                title = "Habit Master",
                description = "Maintain a 3-workout streak without unexcused misses.",
                badgeIconName = "bolt",
                xpReward = 90,
                isUnlocked = false,
                currentProgress = 0,
                targetProgress = 3
            ),
            AchievementEntity(
                id = "comeback_hero",
                title = "Phoenix Rise",
                description = "Return to training and complete a workout session.",
                badgeIconName = "auto_awesome",
                xpReward = 50,
                isUnlocked = false,
                currentProgress = 0,
                targetProgress = 1
            ),
            AchievementEntity(
                id = "monthly_milestone",
                title = "Titan of Discipline",
                description = "Reach 15 total completed workouts in your journey.",
                badgeIconName = "workspace_premium",
                xpReward = 150,
                isUnlocked = false,
                currentProgress = 0,
                targetProgress = 15
            )
        )
    }

    fun getDefaultQuests(periodKey: String): List<QuestEntity> {
        return listOf(
            QuestEntity(
                id = "quest_daily_workout_${periodKey}",
                title = "Complete Today's Session",
                description = "Log any qualifying workout session today.",
                period = QuestPeriod.DAILY.name,
                targetValue = 1,
                currentValue = 0,
                xpReward = 15,
                isCompleted = false,
                isClaimed = false,
                periodKey = periodKey
            ),
            QuestEntity(
                id = "quest_daily_sets_${periodKey}",
                title = "Volume Vanguard",
                description = "Log at least 6 completed sets today.",
                period = QuestPeriod.DAILY.name,
                targetValue = 6,
                currentValue = 0,
                xpReward = 15,
                isCompleted = false,
                isClaimed = false,
                periodKey = periodKey
            ),
            QuestEntity(
                id = "quest_weekly_workouts_${periodKey.substring(0, minOf(7, periodKey.length))}",
                title = "Weekly Commitment",
                description = "Complete 3 workouts this week.",
                period = QuestPeriod.WEEKLY.name,
                targetValue = 3,
                currentValue = 0,
                xpReward = 50,
                isCompleted = false,
                isClaimed = false,
                periodKey = periodKey.substring(0, minOf(7, periodKey.length))
            )
        )
    }
}
