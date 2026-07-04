package com.example.fitnessmobileapp.ui.nutrition

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.fitnessmobileapp.R
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.widget.GridLayout

private val menuList = listOf(
    NutritionItem(
        day = 1,
        breakfastStd = "1 tách trà hoặc cà phê đen không đường và sữa\nCháo bột yến mạch",
        snackStd = "Bất kỳ loại hạt không muối nào\n1 miếng phô mai (mặn hoặc ít béo thì tốt hơn)",
        lunchStd = "2 miếng bánh mì nướng ngũ cốc nguyên hạt với rau (hành tây, cà chua, rau diếp, v.v.)\nBất kỳ loại thịt nạc nướng hoặc luộc (thịt bò, ức gà, thịt cừu, sườn nướng, v.v.)",
        dinnerStd = "Salad gà (ức gà, nướng hoặc luộc, với một số loại trái cây hoặc rau quả)",
        breakfastVeg = "1 tách trà hoặc cà phê đen không đường và sữa\nCháo bột yến mạch",
        snackVeg = "1 miếng phô mai (mặn hoặc ít béo thì tốt hơn)\nMột số loại hạt (bất kỳ loại hạt nào không có muối, hạnh nhân, hạt hồ trăn, hạt điều, quả phi, v.v.)",
        lunchVeg = "2 miếng bánh mì nướng ngũ cốc nguyên hạt với rau (hành tây, cà chua, rau diếp, v.v.)\nSalad trái cây (táo, lê, cam, bưởi, dứa, v.v.)",
        dinnerVeg = "Salad rau (cà rốt, cà chua, ớt ngọt, dưa chuột, v.v.)\nHạt diêm mạch đã nấu hoặc nướng chín",
        breakfastAlt = "1 tách trà hoặc cà phê đen không đường\nTrứng luộc hoặc chiên ít dầu",
        snackAlt = "Sữa chua ít béo\n1 quả táo hoặc lê",
        lunchAlt = "Cơm gạo lứt\nCá hồi nướng với rau củ",
        dinnerAlt = "Súp rau củ\nBánh mì nguyên cám",
        breakfastVegAlt = "Sinh tố rau xanh (cải bó xôi, chuối, sữa hạnh nhân)\nBánh mì nguyên cám",
        snackVegAlt = "Chuối\nHạt hạnh nhân",
        lunchVegAlt = "Đậu hũ xào rau củ\nCơm gạo lứt",
        dinnerVegAlt = "Canh rau củ\nĐậu lăng nấu chín"
    ),
    NutritionItem(
        day = 2,
        breakfastStd = "Trứng bác với rau cải bó xôi\nBánh mì ngũ cốc nguyên hạt nướng",
        snackStd = "1 quả chuối\nSữa chua ít đường",
        lunchStd = "Salad ức gà nướng với rau xanh\nDầu ô liu và giấm balsamic",
        dinnerStd = "Cá hồi nướng với măng tây\nKhoai lang hấp",
        breakfastVeg = "Sinh tố chuối và bơ đậu phộng\nYến mạch nấu với sữa hạnh nhân",
        snackVeg = "Cà rốt và dưa chuột thái lát\nHummus",
        lunchVeg = "Đậu hũ xào sả ớt\nCơm gạo lứt",
        dinnerVeg = "Canh bí đỏ đậu đỏ\nBánh mì nguyên cám",
        breakfastAlt = "Pancake yến mạch với mật ong\nTrà xanh",
        snackAlt = "Một nắm quả việt quất\nHạt óc chó",
        lunchAlt = "Bún bò với rau sống\nNước lọc hoặc trà",
        dinnerAlt = "Gà luộc với rau củ luộc\nCanh cà chua trứng",
        breakfastVegAlt = "Bánh mì nướng với bơ\nSữa đậu nành",
        snackVegAlt = "Táo và bơ đậu phộng\nNước lọc",
        lunchVegAlt = "Mì soba xào rau củ\nNước cam ép",
        dinnerVegAlt = "Súp đậu lăng\nBánh mì nguyên cám nướng"
    ),
    NutritionItem(
        day = 3,
        breakfastStd = "Yến mạch nấu với sữa ít béo và quả mọng\nTrứng luộc",
        snackStd = "1 quả táo\nPhô mai ít béo",
        lunchStd = "Cơm gạo lứt với gà nướng\nRau xào tỏi",
        dinnerStd = "Canh cá chua\nRau muống xào tỏi",
        breakfastVeg = "Yến mạch với hạt chia và quả mọng\nSữa hạnh nhân",
        snackVeg = "Nho tươi\nHạt điều không muối",
        lunchVeg = "Cơm gạo lứt đậu đen\nRau cải xào tỏi",
        dinnerVeg = "Canh chua chay\nĐậu hũ chiên sả",
        breakfastAlt = "Smoothie bowl với granola\nTrà thảo mộc",
        snackAlt = "Dâu tây tươi\nSữa chua Hy Lạp",
        lunchAlt = "Bánh mì kẹp cá ngừ và rau\nNước chanh",
        dinnerAlt = "Tôm hấp với bông cải xanh\nCơm gạo lứt",
        breakfastVegAlt = "Bánh mì nguyên cám với mứt trái cây\nSữa đậu nành ít đường",
        snackVegAlt = "Cam tươi\nHạt hướng dương",
        lunchVegAlt = "Salad đậu chickpea với rau xanh\nDầu ô liu",
        dinnerVegAlt = "Nấm xào bơ tỏi\nCơm gạo lứt"
    ),
    NutritionItem(
        day = 4,
        breakfastStd = "Bánh mì nguyên cám với trứng ốp la\nSữa ít béo",
        snackStd = "Hạt hạnh nhân\nQuả lê",
        lunchStd = "Phở bò tái nạm ít nước béo\nRau sống",
        dinnerStd = "Ức gà áp chảo\nSalad rau xanh dầu ô liu",
        breakfastVeg = "Bánh mì nguyên cám với bơ và cà chua\nTrà xanh",
        snackVeg = "Dưa hấu\nHạt bí rang không muối",
        lunchVeg = "Mì ống nguyên cám sốt cà chua và rau củ\nSalad xanh",
        dinnerVeg = "Đậu phụ sốt nấm\nCơm gạo lứt",
        breakfastAlt = "Cháo yến mạch mặn với trứng\nTrà gừng",
        snackAlt = "Việt quất tươi\nPhô mai cottage",
        lunchAlt = "Cơm tấm sườn nướng ít mỡ\nDưa chua",
        dinnerAlt = "Cá thu kho gừng\nRau muống luộc",
        breakfastVegAlt = "Pancake chuối yến mạch\nMật ong nguyên chất",
        snackVegAlt = "Lê tươi\nHạt macadamia",
        lunchVegAlt = "Canh đậu hũ cà chua\nCơm gạo lứt",
        dinnerVegAlt = "Bông cải xanh hấp với dầu ô liu\nĐậu đỏ nấu chín"
    ),
    NutritionItem(
        day = 5,
        breakfastStd = "Cháo yến mạch với chuối và mật ong\nTrứng luộc",
        snackStd = "Sữa chua Hy Lạp\nQuả mọng tươi",
        lunchStd = "Bún gà\nRau sống và giá đỗ",
        dinnerStd = "Cá basa nướng\nKhoai lang nghiền",
        breakfastVeg = "Smoothie xoài chuối với sữa dừa\nHạt chia ngâm",
        snackVeg = "Dứa tươi thái miếng\nDừa khô không đường",
        lunchVeg = "Cơm chiên rau củ ít dầu\nSalad dưa chuột cà chua",
        dinnerVeg = "Súp bí đỏ coconut\nBánh mì nguyên cám",
        breakfastAlt = "Trứng bác với cà chua và hành\nBánh mì nguyên cám",
        snackAlt = "Hạt óc chó\n1 quả cam",
        lunchAlt = "Cơm gạo lứt cá ngừ\nRau xào",
        dinnerAlt = "Gà hầm khoai tây\nSalad xanh",
        breakfastVegAlt = "Yến mạch với nho khô và quế\nSữa hạnh nhân ấm",
        snackVegAlt = "Xoài tươi\nHạt chia",
        lunchVegAlt = "Đậu lăng xào rau củ\nCơm gạo lứt",
        dinnerVegAlt = "Canh nấm đông cô\nĐậu hũ hấp gừng"
    ),
    NutritionItem(
        day = 6,
        breakfastStd = "Sandwich ức gà và rau xanh\nNước cam ép tươi",
        snackStd = "Cà rốt baby\nHummus",
        lunchStd = "Cơm gạo lứt cá hồi nướng\nMăng tây hấp",
        dinnerStd = "Bò xào bông cải xanh\nCơm gạo lứt",
        breakfastVeg = "Bánh mì nguyên cám với avocado nghiền\nTrứng lòng đào",
        snackVeg = "Dâu tây tươi\nSữa chua ít đường",
        lunchVeg = "Đậu chickpea cà ri\nCơm gạo lứt",
        dinnerVeg = "Ratatouille rau củ\nBánh mì nguyên cám",
        breakfastAlt = "Cháo gạo lứt với gừng\nTrứng hấp",
        snackAlt = "Nho tươi\nPhô mai ít béo",
        lunchAlt = "Mì xào hải sản\nSalad xanh",
        dinnerAlt = "Ức gà nướng chanh\nKhoai lang nướng",
        breakfastVegAlt = "Granola với sữa hạnh nhân\nQuả mọng tươi",
        snackVegAlt = "Chuối\nBơ đậu phộng tự nhiên",
        lunchVegAlt = "Salad quinoa với rau xanh\nDầu ô liu",
        dinnerVegAlt = "Đậu đen nấu với ớt chuông\nCơm gạo lứt"
    ),
    NutritionItem(
        day = 7,
        breakfastStd = "Trứng chiên rau cải bó xôi\nBánh mì nguyên cám nướng",
        snackStd = "1 quả táo\nHạt điều không muối",
        lunchStd = "Salad cá ngừ với rau xanh\nBánh mì nguyên cám",
        dinnerStd = "Tôm nướng với tỏi\nCơm gạo lứt",
        breakfastVeg = "Bánh crepe yến mạch với trái cây\nSữa đậu nành",
        snackVeg = "Thanh long tươi\nHạt bí không muối",
        lunchVeg = "Đậu hũ sốt teriyaki\nCơm gạo lứt",
        dinnerVeg = "Canh rong biển đậu hũ\nRau củ luộc",
        breakfastAlt = "Yến mạch với hạt lanh và mật ong\nTrà xanh",
        snackAlt = "Lê tươi\nSữa chua Hy Lạp",
        lunchAlt = "Phở gà ít nước béo\nRau sống",
        dinnerAlt = "Cá điêu hồng hấp gừng\nRau xào tỏi",
        breakfastVegAlt = "Sinh tố rau cải và chuối\nHạt chia",
        snackVegAlt = "Nho xanh\nHạt hạnh nhân",
        lunchVegAlt = "Mì soba lạnh với rau\nNước tương ít muối",
        dinnerVegAlt = "Súp đậu lăng cà ri\nBánh mì nguyên cám"
    ),
    NutritionItem(
        day = 8,
        breakfastStd = "Yến mạch với quả mọng và hạt chia\nTrà thảo mộc",
        snackStd = "Sữa chua ít béo\nHạt óc chó",
        lunchStd = "Cơm gà luộc với rau luộc\nNước luộc gà",
        dinnerStd = "Cá thu nướng\nSalad dưa chuột cà chua",
        breakfastVeg = "Bánh mì nguyên cám với đậu phộng\nChuối",
        snackVeg = "Việt quất tươi\nHạt hướng dương",
        lunchVeg = "Canh đậu hũ cải thảo\nCơm gạo lứt",
        dinnerVeg = "Nấm xào ớt chuông\nMì nguyên cám",
        breakfastAlt = "Trứng luộc với bánh mì nguyên cám\nNước chanh mật ong",
        snackAlt = "Cam tươi\nPhô mai ít béo",
        lunchAlt = "Bún bò Huế ít cay\nRau sống",
        dinnerAlt = "Ức gà hầm khoai tây\nBông cải xanh hấp",
        breakfastVegAlt = "Pancake chuối với syrup phong\nTrà hoa cúc",
        snackVegAlt = "Dứa tươi\nDừa khô",
        lunchVegAlt = "Đậu đỏ nấu cà ri\nCơm gạo lứt",
        dinnerVegAlt = "Đậu hũ nướng với rau củ\nSalad xanh"
    ),
    NutritionItem(
        day = 9,
        breakfastStd = "Sandwich cá ngừ và rau xanh\nSữa ít béo",
        snackStd = "Dưa hấu\nHạt bí",
        lunchStd = "Mì quảng gà\nRau sống",
        dinnerStd = "Bò nướng sa tế\nRau xà lách",
        breakfastVeg = "Sinh tố dâu tây chuối\nHạt lanh",
        snackVeg = "Táo tươi\nBơ hạnh nhân",
        lunchVeg = "Cơm chiên đậu hũ rau củ\nSalad xanh",
        dinnerVeg = "Canh rau củ thập cẩm\nĐậu xanh nấu chín",
        breakfastAlt = "Cháo cá với gừng\nTrà gừng",
        snackAlt = "Dâu tây\nSữa chua Hy Lạp",
        lunchAlt = "Cơm gạo lứt tôm xào\nRau muống luộc",
        dinnerAlt = "Cá basa kho tiêu\nBắp cải luộc",
        breakfastVegAlt = "Yến mạch với nho khô\nSữa hạnh nhân",
        snackVegAlt = "Lê tươi\nHạt macadamia",
        lunchVegAlt = "Salad rau củ với hạt quinoa\nDầu ô liu chanh",
        dinnerVegAlt = "Canh nấm đông cô rau củ\nCơm gạo lứt"
    ),
    NutritionItem(
        day = 10,
        breakfastStd = "Trứng hấp với cà chua\nBánh mì nguyên cám",
        snackStd = "Quả lê\nHạt hạnh nhân",
        lunchStd = "Cơm tấm sườn nướng\nDưa chua",
        dinnerStd = "Gà luộc nước mắm gừng\nRau luộc",
        breakfastVeg = "Bánh mì với bơ và cà chua\nTrà thảo mộc",
        snackVeg = "Cam tươi\nHạt điều",
        lunchVeg = "Đậu lăng nấu với rau củ\nBánh mì nguyên cám",
        dinnerVeg = "Đậu hũ chiên sả ớt\nCơm gạo lứt",
        breakfastAlt = "Granola với sữa và quả mọng\nTrà xanh",
        snackAlt = "Chuối\nHạt óc chó",
        lunchAlt = "Bún riêu cua ít béo\nRau sống",
        dinnerAlt = "Cá ngừ nướng với rau\nKhoai lang nghiền",
        breakfastVegAlt = "Smoothie bowl granola trái cây\nSữa dừa",
        snackVegAlt = "Nho tươi\nHạt hướng dương",
        lunchVegAlt = "Mì nguyên cám sốt rau củ\nSalad xanh",
        dinnerVegAlt = "Bông cải xanh hấp dầu ô liu\nĐậu đỏ nấu chín"
    ),
    NutritionItem(
        day = 11,
        breakfastStd = "Cháo gạo lứt với gà xé\nTrứng luộc",
        snackStd = "Sữa chua ít đường\nDâu tây",
        lunchStd = "Salad cá hồi với rau xanh\nBánh mì nguyên cám",
        dinnerStd = "Bò xào măng\nCơm gạo lứt",
        breakfastVeg = "Yến mạch với hạt chia và xoài\nSữa hạnh nhân",
        snackVeg = "Việt quất\nHạt bí rang",
        lunchVeg = "Đậu chickpea xào rau củ\nCơm gạo lứt",
        dinnerVeg = "Canh bí đao đậu hũ\nRau cải luộc",
        breakfastAlt = "Trứng ốp la với rau cải\nTrà gừng mật ong",
        snackAlt = "Táo xanh\nPhô mai",
        lunchAlt = "Mì xào bò rau củ\nSalad xanh",
        dinnerAlt = "Tôm hấp với bông cải\nCơm gạo lứt",
        breakfastVegAlt = "Bánh mì nguyên cám với mứt\nSữa đậu nành",
        snackVegAlt = "Dứa\nDừa khô",
        lunchVegAlt = "Salad đậu hũ rau xanh\nDầu ô liu",
        dinnerVegAlt = "Súp cà rốt gừng\nBánh mì nguyên cám"
    ),
    NutritionItem(
        day = 12,
        breakfastStd = "Bánh mì nguyên cám với trứng và rau\nNước cam ép",
        snackStd = "Hạt macadamia\nQuả mọng hỗn hợp",
        lunchStd = "Cơm gạo lứt gà nướng chanh\nRau xào",
        dinnerStd = "Cá điêu hồng kho gừng\nRau muống xào",
        breakfastVeg = "Pancake yến mạch chuối\nSyrup phong tự nhiên",
        snackVeg = "Kiwi tươi\nHạt lanh",
        lunchVeg = "Canh chua chay\nĐậu hũ chiên",
        dinnerVeg = "Nấm đông cô xào rau củ\nCơm gạo lứt",
        breakfastAlt = "Sinh tố protein chuối\nBánh mì nguyên cám",
        snackAlt = "Lê\nSữa chua",
        lunchAlt = "Phở bò ít nước béo\nRau sống",
        dinnerAlt = "Ức gà nướng mật ong\nKhoai lang nướng",
        breakfastVegAlt = "Cháo yến mạch với hạt\nTrà thảo mộc",
        snackVegAlt = "Chuối\nBơ hạnh nhân",
        lunchVegAlt = "Đậu đen xào ớt chuông\nCơm gạo lứt",
        dinnerVegAlt = "Canh rau củ đậu lăng\nBánh mì nguyên cám"
    ),
    NutritionItem(
        day = 13,
        breakfastStd = "Trứng bác với nấm và rau cải\nBánh mì nguyên cám",
        snackStd = "Cà rốt baby\nHummus",
        lunchStd = "Bún chả ít mỡ\nRau sống",
        dinnerStd = "Cá hồi áp chảo với măng tây\nSalad xanh",
        breakfastVeg = "Sinh tố rau xanh chuối\nHạt chia ngâm",
        snackVeg = "Nho đen\nHạt điều",
        lunchVeg = "Cơm gạo lứt đậu hũ sốt tương\nRau cải xanh",
        dinnerVeg = "Súp khoai lang coconut\nBánh mì nguyên cám",
        breakfastAlt = "Yến mạch mặn với trứng\nTrà xanh",
        snackAlt = "Dâu tây\nPhô mai cottage",
        lunchAlt = "Cơm gà xối mỡ ít mỡ\nDưa leo",
        dinnerAlt = "Tôm nướng tỏi bơ\nCơm gạo lứt",
        breakfastVegAlt = "Bánh mì với avocado và cà chua\nSữa đậu nành",
        snackVegAlt = "Xoài\nHạt hướng dương",
        lunchVegAlt = "Mì soba xào rau củ\nNước tương",
        dinnerVegAlt = "Đậu hũ nấu canh nấm\nCơm gạo lứt"
    ),
    NutritionItem(
        day = 14,
        breakfastStd = "Yến mạch với quả mọng và hạt lanh\nSữa ít béo",
        snackStd = "Hạt hướng dương\n1 quả cam",
        lunchStd = "Salad tôm với rau xanh\nBánh mì nguyên cám",
        dinnerStd = "Gà nướng mật ong tỏi\nKhoai tây nghiền ít bơ",
        breakfastVeg = "Bánh mì nướng với đậu phộng nghiền\nChuối",
        snackVeg = "Dưa hấu\nHạt bí",
        lunchVeg = "Đậu lăng cà ri với cơm gạo lứt\nRau cải xào",
        dinnerVeg = "Rau củ nướng với dầu ô liu\nHạt quinoa",
        breakfastAlt = "Pancake protein với trái cây\nTrà thảo mộc",
        snackAlt = "Táo\nHạt óc chó",
        lunchAlt = "Cháo cá thịt\nRau xào",
        dinnerAlt = "Cá thu kho riềng\nRau muống luộc",
        breakfastVegAlt = "Granola với sữa hạnh nhân\nTrái cây tươi",
        snackVegAlt = "Lê\nHạt macadamia",
        lunchVegAlt = "Salad chickpea dưa chuột\nDầu ô liu",
        dinnerVegAlt = "Canh đậu đỏ rau củ\nCơm gạo lứt"
    ),
    NutritionItem(
        day = 15,
        breakfastStd = "Trứng chiên với cà chua và hành\nBánh mì nguyên cám",
        snackStd = "Sữa chua Hy Lạp\nHạt hạnh nhân",
        lunchStd = "Cơm gạo lứt cá ngừ đóng hộp\nSalad rau xanh",
        dinnerStd = "Bò hầm rau củ\nBánh mì nguyên cám",
        breakfastVeg = "Sinh tố xoài chuối với hạt lanh\nBánh mì nguyên cám",
        snackVeg = "Dâu tây\nSữa chua không đường",
        lunchVeg = "Đậu hũ xào sốt mè\nCơm gạo lứt",
        dinnerVeg = "Canh rong biển đậu hũ\nRau cải luộc",
        breakfastAlt = "Cháo yến mạch với hạt và mật ong\nTrà xanh",
        snackAlt = "Việt quất\nPhô mai",
        lunchAlt = "Mì spaghetti nguyên cám sốt thịt\nSalad xanh",
        dinnerAlt = "Gà hấp gừng hành\nCơm gạo lứt",
        breakfastVegAlt = "Bánh mì với mứt việt quất\nSữa đậu nành",
        snackVegAlt = "Cam\nHạt chia",
        lunchVegAlt = "Đậu đen xào ớt chuông\nCơm gạo lứt",
        dinnerVegAlt = "Nấm hầm với rau củ\nBánh mì nguyên cám"
    ),
    NutritionItem(
        day = 16,
        breakfastStd = "Sandwich gà với rau xanh\nNước ép trái cây tươi",
        snackStd = "Quả lê\nHạt điều",
        lunchStd = "Phở gà ít béo\nRau sống",
        dinnerStd = "Cá basa nướng giấy bạc\nKhoai lang hấp",
        breakfastVeg = "Yến mạch với dừa và xoài\nSữa hạnh nhân",
        snackVeg = "Kiwi\nHạt lanh",
        lunchVeg = "Canh đậu hũ cà chua\nCơm gạo lứt",
        dinnerVeg = "Đậu chickpea nướng với rau củ\nSalad xanh",
        breakfastAlt = "Trứng luộc với rau cải\nTrà gừng",
        snackAlt = "Nho tươi\nSữa chua",
        lunchAlt = "Bún riêu tôm\nRau sống",
        dinnerAlt = "Ức gà nướng với rau củ\nCơm gạo lứt",
        breakfastVegAlt = "Smoothie dâu tây chuối\nHạt chia",
        snackVegAlt = "Táo\nBơ hạnh nhân",
        lunchVegAlt = "Mì nguyên cám sốt đậu lăng\nSalad xanh",
        dinnerVegAlt = "Súp bí đỏ gừng\nBánh mì nguyên cám"
    ),
    NutritionItem(
        day = 17,
        breakfastStd = "Cháo gạo lứt với trứng và gừng\nTrà thảo mộc",
        snackStd = "Táo xanh\nHạt óc chó",
        lunchStd = "Cơm gà luộc rau\nCanh rau củ",
        dinnerStd = "Tôm xào bông cải xanh\nCơm gạo lứt",
        breakfastVeg = "Bánh mì nguyên cám với hummus\nCà chua bi",
        snackVeg = "Chuối\nHạt hướng dương",
        lunchVeg = "Đậu lăng soup với bánh mì\nSalad xanh",
        dinnerVeg = "Đậu hũ nướng với ớt chuông\nCơm gạo lứt",
        breakfastAlt = "Yến mạch protein với trái cây\nSữa ít béo",
        snackAlt = "Dưa hấu\nPhô mai ít béo",
        lunchAlt = "Cơm tấm sườn ít mỡ\nSalad rau",
        dinnerAlt = "Cá điêu hồng sốt cà chua\nRau xào",
        breakfastVegAlt = "Pancake bí đỏ\nSyrup phong",
        snackVegAlt = "Dứa\nHạt macadamia",
        lunchVegAlt = "Salad quinoa rau củ\nDầu ô liu",
        dinnerVegAlt = "Canh nấm rau củ\nĐậu đỏ nấu"
    ),
    NutritionItem(
        day = 18,
        breakfastStd = "Trứng ốp la với rau bina\nBánh mì nguyên cám",
        snackStd = "Dâu tây\nSữa chua ít béo",
        lunchStd = "Salad bò với rau xanh\nBánh mì nguyên cám",
        dinnerStd = "Cá hồi nướng miso\nCơm gạo lứt",
        breakfastVeg = "Sinh tố việt quất và cải bó xôi\nHạt lanh",
        snackVeg = "Nho đỏ\nHạt điều",
        lunchVeg = "Cơm chiên rau củ đậu hũ ít dầu\nSalad xanh",
        dinnerVeg = "Canh đậu đỏ coconut\nBánh mì nguyên cám",
        breakfastAlt = "Granola với sữa\nQuả mọng tươi",
        snackAlt = "Cam\nHạt hạnh nhân",
        lunchAlt = "Mì xào hải sản ít dầu\nSalad xanh",
        dinnerAlt = "Gà nướng sả ớt\nKhoai tây nướng",
        breakfastVegAlt = "Cháo yến mạch với hoa quả\nTrà xanh",
        snackVegAlt = "Lê\nHạt bí",
        lunchVegAlt = "Đậu chickpea sốt cà chua\nCơm gạo lứt",
        dinnerVegAlt = "Bông cải trắng nướng\nĐậu lăng soup"
    ),
    NutritionItem(
        day = 19,
        breakfastStd = "Yến mạch với hạt chia và chuối\nTrà xanh",
        snackStd = "Hạt bí rang\nViệt quất",
        lunchStd = "Cơm gạo lứt cá thu kho\nRau muống luộc",
        dinnerStd = "Ức gà hầm nấm\nSalad rau xanh",
        breakfastVeg = "Bánh mì nướng với avocado\nTrứng lòng đào",
        snackVeg = "Thanh long\nHạt hướng dương",
        lunchVeg = "Đậu hũ sốt mè đen\nCơm gạo lứt",
        dinnerVeg = "Nấm xào rau củ\nMì nguyên cám",
        breakfastAlt = "Trứng hấp với cà chua\nTrà gừng mật ong",
        snackAlt = "Táo đỏ\nPhô mai",
        lunchAlt = "Bún thịt nướng ít mỡ\nRau sống",
        dinnerAlt = "Tôm nướng với salad\nCơm gạo lứt",
        breakfastVegAlt = "Smoothie rau cải xanh\nBánh mì nguyên cám",
        snackVegAlt = "Xoài\nHạt óc chó",
        lunchVegAlt = "Mì soba với đậu hũ\nRau xanh",
        dinnerVegAlt = "Súp đậu lăng cà ri\nBánh mì naan nguyên cám"
    ),
    NutritionItem(
        day = 20,
        breakfastStd = "Bánh mì với trứng ốp la và cà chua\nNước cam tươi",
        snackStd = "Sữa chua Hy Lạp\nHạt macadamia",
        lunchStd = "Cháo cá với rau\nTrứng luộc",
        dinnerStd = "Bò nướng với salad\nKhoai lang nướng",
        breakfastVeg = "Yến mạch với nho khô và quế\nSữa hạnh nhân ấm",
        snackVeg = "Dứa\nHạt lanh",
        lunchVeg = "Canh chua đậu hũ\nCơm gạo lứt",
        dinnerVeg = "Rau củ nướng với quinoa\nSalad xanh",
        breakfastAlt = "Pancake yến mạch với mật ong\nTrà thảo mộc",
        snackAlt = "Chuối\nHạt hướng dương",
        lunchAlt = "Phở bò ít nước béo\nRau sống",
        dinnerAlt = "Cá ngừ sốt cà chua\nCơm gạo lứt",
        breakfastVegAlt = "Bánh mì với mứt dâu tây\nSữa đậu nành",
        snackVegAlt = "Kiwi\nHạt điều",
        lunchVegAlt = "Salad đậu đen với ớt chuông\nDầu ô liu",
        dinnerVegAlt = "Canh nấm đông cô\nĐậu hũ hấp"
    ),
    NutritionItem(
        day = 21,
        breakfastStd = "Trứng bác với nấm và cà chua\nBánh mì nguyên cám",
        snackStd = "Lê tươi\nHạt hạnh nhân",
        lunchStd = "Salad ức gà với quinoa\nDầu ô liu chanh",
        dinnerStd = "Cá hồi nướng với khoai lang\nMăng tây hấp",
        breakfastVeg = "Sinh tố dâu tây và sữa dừa\nHạt chia ngâm",
        snackVeg = "Cam tươi\nHạt bí",
        lunchVeg = "Đậu lăng cà ri với cơm\nRau cải xào",
        dinnerVeg = "Đậu hũ chiên giòn với sốt tương\nCơm gạo lứt",
        breakfastAlt = "Cháo gạo lứt với gà\nTrà xanh",
        snackAlt = "Việt quất\nSữa chua",
        lunchAlt = "Bún gà với rau sống\nNước lọc",
        dinnerAlt = "Tôm hấp với bông cải\nCơm gạo lứt",
        breakfastVegAlt = "Granola với quả mọng\nSữa hạnh nhân",
        snackVegAlt = "Nho tươi\nHạt macadamia",
        lunchVegAlt = "Mì nguyên cám với đậu chickpea\nSalad xanh",
        dinnerVegAlt = "Canh rau củ đậu đỏ\nBánh mì nguyên cám"
    ),
    NutritionItem(
        day = 22,
        breakfastStd = "Yến mạch với chuối và mật ong\nTrứng luộc",
        snackStd = "Cà rốt và cần tây\nHummus",
        lunchStd = "Cơm gạo lứt với cá thu nướng\nRau xào tỏi",
        dinnerStd = "Gà hầm khoai tây rau củ\nSalad xanh",
        breakfastVeg = "Bánh mì nướng với bơ và mật ong\nTrà thảo mộc",
        snackVeg = "Dâu tây\nHạt óc chó",
        lunchVeg = "Đậu chickpea nướng với rau củ\nCơm gạo lứt",
        dinnerVeg = "Súp miso với đậu hũ\nCơm gạo lứt",
        breakfastAlt = "Trứng chiên với rau cải\nSữa ít béo",
        snackAlt = "Táo\nPhô mai ít béo",
        lunchAlt = "Mì xào thịt bò rau củ\nSalad xanh",
        dinnerAlt = "Cá điêu hồng hấp gừng\nRau muống xào",
        breakfastVegAlt = "Smoothie chuối hạnh nhân\nBánh mì nguyên cám",
        snackVegAlt = "Xoài\nHạt hướng dương",
        lunchVegAlt = "Salad đậu lăng với rau xanh\nDầu ô liu",
        dinnerVegAlt = "Bông cải xanh hấp\nĐậu đỏ nấu với cà chua"
    ),
    NutritionItem(
        day = 23,
        breakfastStd = "Bánh mì nguyên cám với trứng luộc\nNước ép cà rốt",
        snackStd = "Sữa chua\nHạt điều",
        lunchStd = "Phở gà ít béo\nRau sống ngò gai",
        dinnerStd = "Bò xào cần tây\nCơm gạo lứt",
        breakfastVeg = "Yến mạch với hạt lanh và chuối\nSữa đậu nành",
        snackVeg = "Việt quất\nHạt bí",
        lunchVeg = "Canh đậu hũ nấm hương\nCơm gạo lứt",
        dinnerVeg = "Đậu đen xào ớt chuông ngô\nMì nguyên cám",
        breakfastAlt = "Smoothie protein chuối\nBánh mì nguyên cám",
        snackAlt = "Lê\nHạt hạnh nhân",
        lunchAlt = "Cơm gà hầm thuốc bắc\nRau xào",
        dinnerAlt = "Tôm sốt cà chua\nCơm gạo lứt",
        breakfastVegAlt = "Pancake chuối với trái cây\nTrà hoa cúc",
        snackVegAlt = "Dứa\nHạt lanh",
        lunchVegAlt = "Đậu hũ xào sả với cơm\nSalad xanh",
        dinnerVegAlt = "Canh bí đao đậu lăng\nBánh mì nguyên cám"
    ),
    NutritionItem(
        day = 24,
        breakfastStd = "Trứng ốp la với rau bina\nBánh mì nguyên cám",
        snackStd = "Cam tươi\nHạt óc chó",
        lunchStd = "Salad cá ngừ quinoa\nDầu ô liu",
        dinnerStd = "Cá hồi sốt chanh bơ\nKhoai lang nghiền",
        breakfastVeg = "Sinh tố xanh cải bó xôi\nHạt chia",
        snackVeg = "Nho xanh\nHạt macadamia",
        lunchVeg = "Cơm gạo lứt đậu hũ nướng\nRau cải xào",
        dinnerVeg = "Nấm hầm với đậu lăng\nBánh mì nguyên cám",
        breakfastAlt = "Cháo yến mạch với táo\nTrà gừng",
        snackAlt = "Dâu tây\nSữa chua Hy Lạp",
        lunchAlt = "Bún bò xào lá lốt\nRau sống",
        dinnerAlt = "Gà nướng thảo mộc\nSalad rau xanh",
        breakfastVegAlt = "Bánh mì với hummus và rau\nSữa đậu nành",
        snackVegAlt = "Chuối\nBơ đậu phộng",
        lunchVegAlt = "Mì soba lạnh với rau\nNước tương",
        dinnerVegAlt = "Súp đậu đỏ coconut\nCơm gạo lứt"
    ),
    NutritionItem(
        day = 25,
        breakfastStd = "Yến mạch với quả mọng và hạt\nSữa ít béo",
        snackStd = "Hạt hướng dương\nViệt quất",
        lunchStd = "Cơm gạo lứt tôm hấp\nBông cải xanh luộc",
        dinnerStd = "Bò hầm với khoai tây\nBánh mì nguyên cám",
        breakfastVeg = "Bánh mì nguyên cám với bơ\nCà phê đen không đường",
        snackVeg = "Táo\nHạt hạnh nhân",
        lunchVeg = "Đậu lăng soup với rau củ\nBánh mì nguyên cám",
        dinnerVeg = "Đậu hũ xào cà chua\nCơm gạo lứt",
        breakfastAlt = "Trứng hấp với hành và gừng\nTrà thảo mộc",
        snackAlt = "Lê\nPhô mai",
        lunchAlt = "Phở bò ít nước béo\nRau sống",
        dinnerAlt = "Cá basa sốt mè\nCơm gạo lứt",
        breakfastVegAlt = "Granola với sữa dừa\nQuả mọng",
        snackVegAlt = "Dứa\nHạt điều",
        lunchVegAlt = "Salad chickpea với rau\nDầu ô liu chanh",
        dinnerVegAlt = "Canh rau củ đậu đỏ\nBánh mì nguyên cám"
    ),
    NutritionItem(
        day = 26,
        breakfastStd = "Sandwich cá hồi và rau xanh\nNước chanh mật ong",
        snackStd = "Sữa chua Hy Lạp\nHạt lanh",
        lunchStd = "Cơm gà nướng sả\nSalad dưa chuột",
        dinnerStd = "Cá thu kho nghệ\nRau cải luộc",
        breakfastVeg = "Yến mạch với hạt chia và kiwi\nSữa hạnh nhân",
        snackVeg = "Dâu tây\nHạt bí",
        lunchVeg = "Đậu chickpea sốt cà ri\nCơm gạo lứt",
        dinnerVeg = "Rau củ xào với đậu đen\nMì nguyên cám",
        breakfastAlt = "Pancake protein với chuối\nTrà xanh",
        snackAlt = "Cam\nHạt óc chó",
        lunchAlt = "Mì ý sốt thịt nguyên cám\nSalad xanh",
        dinnerAlt = "Tôm hấp gừng hành\nKhoai lang nghiền",
        breakfastVegAlt = "Smoothie cải bó xôi táo\nHạt chia",
        snackVegAlt = "Xoài\nHạt hướng dương",
        lunchVegAlt = "Đậu lăng xào với ớt chuông\nCơm gạo lứt",
        dinnerVegAlt = "Súp bí đỏ gừng nghệ\nBánh mì nguyên cám"
    ),
    NutritionItem(
        day = 27,
        breakfastStd = "Trứng bác với rau và phô mai\nBánh mì nguyên cám",
        snackStd = "Táo đỏ\nHạt điều",
        lunchStd = "Salad hải sản với rau xanh\nBánh mì nguyên cám",
        dinnerStd = "Ức gà nướng với rau củ\nCơm gạo lứt",
        breakfastVeg = "Bánh mì với avocado và hành\nTrà thảo mộc",
        snackVeg = "Việt quất\nHạt macadamia",
        lunchVeg = "Canh bí ngô đậu hũ\nCơm gạo lứt",
        dinnerVeg = "Đậu hũ nướng với sốt miso\nSalad xanh",
        breakfastAlt = "Cháo gạo lứt với cá và gừng\nTrà gừng",
        snackAlt = "Nho tươi\nSữa chua",
        lunchAlt = "Bún cá ít béo\nRau sống",
        dinnerAlt = "Bò xào măng tươi\nCơm gạo lứt",
        breakfastVegAlt = "Yến mạch với trái cây sấy\nSữa đậu nành",
        snackVegAlt = "Lê\nHạt hướng dương",
        lunchVegAlt = "Mì nguyên cám sốt đậu chickpea\nSalad xanh",
        dinnerVegAlt = "Canh đậu lăng rau củ\nBánh mì nguyên cám"
    ),
    NutritionItem(
        day = 28,
        breakfastStd = "Cháo yến mạch với chuối và hạt\nTrà xanh",
        snackStd = "Dưa hấu\nHạt hạnh nhân",
        lunchStd = "Cơm gạo lứt cá hồi sốt\nMăng tây hấp",
        dinnerStd = "Gà nướng thảo mộc\nKhoai lang nướng",
        breakfastVeg = "Sinh tố chuối và đậu phộng\nHạt lanh",
        snackVeg = "Kiwi\nHạt bí",
        lunchVeg = "Đậu đỏ nấu với rau củ\nBánh mì nguyên cám",
        dinnerVeg = "Nấm đông cô xào rau\nCơm gạo lứt",
        breakfastAlt = "Trứng chiên với rau bina\nSữa ít béo",
        snackAlt = "Táo xanh\nPhô mai ít béo",
        lunchAlt = "Phở gà ít nước béo\nRau sống",
        dinnerAlt = "Cá điêu hồng sốt gừng\nRau xào",
        breakfastVegAlt = "Pancake bí đỏ hạt điều\nTrà hoa cúc",
        snackVegAlt = "Chuối\nBơ hạnh nhân",
        lunchVegAlt = "Salad đậu hũ quinoa\nDầu ô liu",
        dinnerVegAlt = "Súp đậu lăng coconut\nCơm gạo lứt"
    ),
    NutritionItem(
        day = 29,
        breakfastStd = "Trứng luộc với bánh mì nguyên cám\nNước ép cà rốt gừng",
        snackStd = "Sữa chua\nViệt quất",
        lunchStd = "Salad thịt bò với rau xanh\nBánh mì nguyên cám",
        dinnerStd = "Tôm nướng muối ớt\nCơm gạo lứt",
        breakfastVeg = "Yến mạch với hạt và quả mọng\nSữa đậu nành",
        snackVeg = "Dứa tươi\nHạt hướng dương",
        lunchVeg = "Cơm gạo lứt đậu hũ sốt cà\nRau cải xào",
        dinnerVeg = "Canh đậu xanh rau củ\nBánh mì nguyên cám",
        breakfastAlt = "Granola với sữa và mật ong\nTrà thảo mộc",
        snackAlt = "Cam\nHạt óc chó",
        lunchAlt = "Bún thịt nướng rau sống\nNước lọc",
        dinnerAlt = "Cá thu nướng với salad\nKhoai lang",
        breakfastVegAlt = "Smoothie xoài dừa\nBánh mì nguyên cám",
        snackVegAlt = "Nho\nHạt macadamia",
        lunchVegAlt = "Đậu chickpea xào ớt chuông\nCơm gạo lứt",
        dinnerVegAlt = "Nấm xào với đậu lăng\nMì nguyên cám"
    ),
    NutritionItem(
        day = 30,
        breakfastStd = "Yến mạch với hạt chia chuối và mật ong\nTrà xanh",
        snackStd = "Hạt hạnh nhân\nDâu tây tươi",
        lunchStd = "Cơm gạo lứt cá hồi nướng\nSalad rau xanh",
        dinnerStd = "Ức gà nướng chanh thảo mộc\nKhoai lang nghiền",
        breakfastVeg = "Sinh tố rau cải chuối hạt lanh\nBánh mì nguyên cám",
        snackVeg = "Táo tươi\nHạt điều không muối",
        lunchVeg = "Đậu lăng cà ri với cơm gạo lứt\nRau cải xào tỏi",
        dinnerVeg = "Đậu hũ nướng với rau củ nướng\nSalad quinoa",
        breakfastAlt = "Trứng bác với rau và cà chua\nSữa ít béo",
        snackAlt = "Lê tươi\nSữa chua Hy Lạp",
        lunchAlt = "Phở bò ít nước béo\nRau sống ngò gai",
        dinnerAlt = "Cá điêu hồng hấp gừng hành\nRau muống xào tỏi",
        breakfastVegAlt = "Bánh mì nguyên cám với avocado\nTrà thảo mộc",
        snackVegAlt = "Chuối\nHạt hướng dương",
        lunchVegAlt = "Salad đậu đen ớt chuông ngô\nDầu ô liu chanh",
        dinnerVegAlt = "Canh bí đỏ đậu đỏ coconut\nCơm gạo lứt"
    )
)

class NutritionFragment : Fragment(R.layout.fragment_nutrition) {

    // Chức năng: khởi tạo giao diện màn Công thức nấu.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.btnShoppingList)?.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ShoppingFragment())
                .addToBackStack(null)
                .commit()
        }

        setupDayGrid(view)
        refreshDayStates(view)
    }

    override fun onResume() {
        super.onResume()
        view?.let {
            refreshDayStates(it)
        }
    }

    // Chức năng: tạo 30 ô ngày bằng code để giao diện gọn và dễ chỉnh.
    private fun setupDayGrid(rootView: View) {
        val gridMealDays = rootView.findViewById<GridLayout>(R.id.gridMealDays)

        gridMealDays.removeAllViews()
        gridMealDays.columnCount = 3

        for (day in 1..30) {
            val dayView = TextView(requireContext()).apply {
                tag = "day_$day"
                text = "Ngày $day"
                gravity = Gravity.CENTER
                includeFontPadding = false
                textSize = 18f
                typeface = Typeface.DEFAULT
                setTextColor(Color.parseColor("#222222"))
                background = createDayBackground(isDone = false)
                elevation = dp(2).toFloat()
                isClickable = true
                isFocusable = true

                setOnClickListener {
                    val detailFragment = NutritionDetailFragment()

                    val bundle = Bundle()
                    bundle.putSerializable("nutrition_data", menuList[day - 1])
                    detailFragment.arguments = bundle

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, detailFragment)
                        .addToBackStack(null)
                        .commit()
                }
            }

            val params = GridLayout.LayoutParams(
                GridLayout.spec(GridLayout.UNDEFINED, 1f),
                GridLayout.spec(GridLayout.UNDEFINED, 1f)
            ).apply {
                width = 0
                height = dp(82)
                setMargins(dp(6), dp(8), dp(6), dp(8))
            }

            gridMealDays.addView(dayView, params)
        }
    }

    // Chức năng: cập nhật trạng thái hoàn thành của các ngày.
    private fun refreshDayStates(rootView: View) {
        val prefs = requireActivity().getSharedPreferences("user_prefs", 0)

        for (day in 1..30) {
            val dayView = rootView.findViewWithTag<TextView>("day_$day")
            val isDone = prefs.getBoolean("day_${day}_done", false)

            updateDayView(dayView, day, isDone)
        }
    }

    // Chức năng: đổi màu ô ngày nếu đã hoàn thành.
    private fun updateDayView(dayView: TextView?, day: Int, isDone: Boolean) {
        if (dayView == null) return

        dayView.text = "Ngày $day"
        dayView.background = createDayBackground(isDone)
        dayView.elevation = dp(2).toFloat()

        if (isDone) {
            dayView.setTextColor(Color.WHITE)
        } else {
            dayView.setTextColor(Color.parseColor("#222222"))
        }
    }

    // Chức năng: tạo nền bo góc cho ô ngày.
    private fun createDayBackground(isDone: Boolean): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dp(12).toFloat()

            if (isDone) {
                setColor(Color.parseColor("#65D96F"))
            } else {
                setColor(Color.WHITE)
            }
        }
    }

    // Chức năng: đổi dp sang pixel.
    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}