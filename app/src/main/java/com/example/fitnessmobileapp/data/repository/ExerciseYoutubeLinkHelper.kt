package com.example.fitnessmobileapp.data.repository

// Chức năng: quản lý link YouTube cho từng bài tập.
// Nếu trong workout_data.json chưa có youtubeUrl thì app sẽ lấy link ở đây.
object ExerciseYoutubeLinkHelper {

    private val youtubeLinks = mapOf(
        // Tay & ngực
        "arms_chest_001" to "https://youtu.be/kLmWN3Qsj0A?si=YuDNLTpEyuYVKILe",
        "Ngực dao động" to "https://youtu.be/kLmWN3Qsj0A?si=YuDNLTpEyuYVKILe",

        "arms_chest_002" to "https://youtu.be/JhX1nBnirNw?si=efPkwvvDv4OBmeDf",
        "Tập cơ tay sau trên ghế" to "https://youtu.be/JhX1nBnirNw?si=efPkwvvDv4OBmeDf",

        "arms_chest_003" to "https://youtu.be/TGQi4VsPIhU?si=epRtwfqDthrUHZua",
        "Chống đẩy" to "https://youtu.be/TGQi4VsPIhU?si=epRtwfqDthrUHZua",

        "arms_chest_004" to "https://youtu.be/OGfFtF-dhrk?si=honElcfPb_9FNPD9",
        "Hít đất chéo" to "https://youtu.be/OGfFtF-dhrk?si=honElcfPb_9FNPD9",

        "arms_chest_005" to "https://youtu.be/3WUUeM07i_Q?si=ANHBza2HhGIMjEuL",
        "Chống đẩy cao tay" to "https://youtu.be/3WUUeM07i_Q?si=ANHBza2HhGIMjEuL",

        "arms_chest_006" to "https://youtu.be/M_uNXxdI018?si=KFy_tqqDQEIoMJ78",
        "Trườn hình ngôi sao" to "https://youtu.be/M_uNXxdI018?si=KFy_tqqDQEIoMJ78",

        "arms_chest_007" to "https://youtu.be/pFrJQ-MyL10?si=5lZ1GBwOWjIVE1QI",
        "Tay cắt kéo" to "https://youtu.be/pFrJQ-MyL10?si=5lZ1GBwOWjIVE1QI",

        "arms_chest_008" to "https://youtu.be/L9IGOcrdcFk?si=qxGib_2rV4wZvDXw",
        "Căng cơ tay trái" to "https://youtu.be/L9IGOcrdcFk?si=qxGib_2rV4wZvDXw",

        "arms_chest_009" to "https://youtu.be/L9IGOcrdcFk?si=zG_4WLURYweqdNH1",
        "Căng cơ tay phải" to "https://youtu.be/L9IGOcrdcFk?si=zG_4WLURYweqdNH1",

        "arms_chest_010" to "https://youtu.be/jw8EXo5h0ec?si=yBcZHK3Qy3W4SXNe",
        "Đứng duỗi cơ tay trước bên trái" to "https://youtu.be/jw8EXo5h0ec?si=yBcZHK3Qy3W4SXNe",

        "arms_chest_011" to "https://youtu.be/jw8EXo5h0ec?si=R97ek2eBzwBDhLN4",
        "Đứng duỗi cơ tay trước bên phải" to "https://youtu.be/jw8EXo5h0ec?si=R97ek2eBzwBDhLN4",

        "arms_chest_012" to "https://youtu.be/rhtadqkrWo0?si=zQOKw38XdaUsyh-m",
        "Đưa khuỷu tay về sau" to "https://youtu.be/rhtadqkrWo0?si=zQOKw38XdaUsyh-m",

        "arms_chest_013" to "https://youtu.be/3kZS8HVFquk?si=7zd3fQ4bdNDNDL5Z",
        "Cuốn tạ chân trái" to "https://youtu.be/3kZS8HVFquk?si=7zd3fQ4bdNDNDL5Z",

        "arms_chest_014" to "https://youtu.be/3kZS8HVFquk?si=EK4s5G3oxuIkyeW0",
        "Cuốn tạ chân phải" to "https://youtu.be/3kZS8HVFquk?si=EK4s5G3oxuIkyeW0",

        "arms_chest_015" to "https://youtu.be/QGnz__47PCo?si=yZwy0HMxoFknx29E",
        "Hít đất vỗ vai" to "https://youtu.be/QGnz__47PCo?si=yZwy0HMxoFknx29E",

        "arms_chest_016" to "https://youtu.be/OI-3e5Dcm-I?si=D3_1oTKerQT5ojKt",
        "Cua bò" to "https://youtu.be/OI-3e5Dcm-I?si=D3_1oTKerQT5ojKt",

        "arms_chest_017" to "https://youtu.be/jWxvty2KROs?si=TXS9rnF9be2KT35N",
        "Chống đẩy bằng đầu gối" to "https://youtu.be/jWxvty2KROs?si=TXS9rnF9be2KT35N",

        "arms_chest_018" to "https://youtu.be/wiyvVpEKOsc?si=vh5KSQ-aY7xonxtS",
        "Đấm móc luân phiên" to "https://youtu.be/wiyvVpEKOsc?si=vh5KSQ-aY7xonxtS",

        "arms_chest_019" to "https://youtu.be/EOf3cGIQpA4?si=i_c6uoEgupK5iz8M",
        "Chống đẩy vào tường" to "https://youtu.be/EOf3cGIQpA4?si=i_c6uoEgupK5iz8M",

        "arms_chest_020" to "https://youtu.be/UCmqw3kKZ38?si=mYE-OFA-gW0g_OAF",
        "Chống đẩy tay hình kim cương" to "https://youtu.be/UCmqw3kKZ38?si=mYE-OFA-gW0g_OAF",

        "arms_chest_021" to "https://youtu.be/pQUsUHvyoI0?si=u5ni109Gx1T-gdTT",
        "Chống đẩy để tay rộng" to "https://youtu.be/pQUsUHvyoI0?si=u5ni109Gx1T-gdTT",

        "arms_chest_022" to "https://youtu.be/reeBHtZJ1ts?si=T6FLh2LCrUrHWv84",
        "Đấm" to "https://youtu.be/reeBHtZJ1ts?si=T6FLh2LCrUrHWv84",

        // Chân
        "legs_001" to "https://youtu.be/2W4ZNSwoW_4?si=z7BEU6GqU7OsN0mT",
        "Bật nhảy" to "https://youtu.be/2W4ZNSwoW_4?si=z7BEU6GqU7OsN0mT",

        "legs_002" to "https://youtu.be/42bFodPahBU?si=yyzN49YZ9zgz2bjb",
        "Gánh đùi" to "https://youtu.be/42bFodPahBU?si=yyzN49YZ9zgz2bjb",

        "legs_003" to "https://youtu.be/VlwBJE1WtOQ?si=u0yBBkNBfgoQjL3O",
        "Nằm nghiên người nâng chân trái" to "https://youtu.be/VlwBJE1WtOQ?si=u0yBBkNBfgoQjL3O",
        "Nằm nghiêng người nâng chân trái" to "https://youtu.be/VlwBJE1WtOQ?si=u0yBBkNBfgoQjL3O",

        "legs_004" to "https://youtu.be/VlwBJE1WtOQ?si=lmlWFTBu5nzera0E",
        "Nằm người người nâng chân phải" to "https://youtu.be/VlwBJE1WtOQ?si=lmlWFTBu5nzera0E",

        "legs_005" to "https://youtu.be/_LGpDtENZ5U?si=jcIspR-BP7-jBm3a",
        "Tấn sau" to "https://youtu.be/_LGpDtENZ5U?si=jcIspR-BP7-jBm3a",

        "legs_006" to "https://youtu.be/4ranVQDqlaU?si=feTzNeU-xuWTOsOY",
        "Lừa đá chân trái" to "https://youtu.be/4ranVQDqlaU?si=feTzNeU-xuWTOsOY",

        "legs_007" to "https://youtu.be/4ranVQDqlaU?si=xgz7PqwZrs3dTjBi",
        "Lừa đá chân phải" to "https://youtu.be/4ranVQDqlaU?si=xgz7PqwZrs3dTjBi",

        "legs_008" to "https://youtu.be/TfcRyYf7WLg?si=fjXgD6X-BwpI_6xs",
        "Dựa tượng duỗi cơ đùi trước trái" to "https://youtu.be/TfcRyYf7WLg?si=fjXgD6X-BwpI_6xs",

        "legs_009" to "https://youtu.be/TfcRyYf7WLg?si=zObpdF1nSooSjE-Z",
        "Dựa tượng duỗi cơ đùi trước phải" to "https://youtu.be/TfcRyYf7WLg?si=zObpdF1nSooSjE-Z",

        "legs_010" to "https://youtu.be/bJms9YyjoBI?si=ukm-A_yzp62yrAHD",
        "Ép đầu gối trái lên ngực" to "https://youtu.be/bJms9YyjoBI?si=ukm-A_yzp62yrAHD",

        "legs_011" to "https://youtu.be/bJms9YyjoBI?si=yAMWUwdP1rUvH9S7",
        "Ép đầu gối phải lên ngực" to "https://youtu.be/bJms9YyjoBI?si=yAMWUwdP1rUvH9S7",

        "legs_012" to "https://youtu.be/9qo48CYN06w?si=6qSi-XOu9aCUL1F-",
        "Cây cầu mông" to "https://youtu.be/9qo48CYN06w?si=6qSi-XOu9aCUL1F-",

        "legs_013" to "https://youtu.be/XEKiRnwBfYA?si=IsJxoi5TVpktIb-9",
        "Đứng tấn" to "https://youtu.be/XEKiRnwBfYA?si=IsJxoi5TVpktIb-9",

        "legs_014" to "https://youtu.be/wQq3ybaLZeA?si=Pg7V2IZFOEHLcIq9",
        "Leo núi" to "https://youtu.be/wQq3ybaLZeA?si=Pg7V2IZFOEHLcIq9",

        "legs_015" to "https://youtu.be/SFSZVKzqnXA?si=NK9tiKNVqz0e8MMm",
        "Đứng tấn chân trái" to "https://youtu.be/SFSZVKzqnXA?si=NK9tiKNVqz0e8MMm",

        "legs_016" to "https://youtu.be/SFSZVKzqnXA?si=BONT5CSe6Qa8PeLU",
        "Đứng tấn chân phải" to "https://youtu.be/SFSZVKzqnXA?si=BONT5CSe6Qa8PeLU",

        "legs_017" to "https://youtu.be/MjFb2MyaNjs?si=7ma-IKGTW3l6aDXi",
        "Duỗi cơ đùi trong khi đứng" to "https://youtu.be/MjFb2MyaNjs?si=7ma-IKGTW3l6aDXi",

        "legs_018" to "https://youtu.be/ryNlb_0GmAw?si=8l4uS1manQyeYq6K",
        "Duỗi vặn sống lưng bên trái" to "https://youtu.be/ryNlb_0GmAw?si=8l4uS1manQyeYq6K",

        "legs_019" to "https://youtu.be/ryNlb_0GmAw?si=fX10LCQr3sTROcWs",
        "Duỗi vặn sống lưng bên phải" to "https://youtu.be/ryNlb_0GmAw?si=fX10LCQr3sTROcWs",

        "legs_020" to "https://youtu.be/GQa_N7wft7M?si=q-xlFQpHQHm5DcRM",
        "Nâng bắp chân dựa tường" to "https://youtu.be/GQa_N7wft7M?si=q-xlFQpHQHm5DcRM",

        "legs_021" to "https://youtu.be/Hcy81KUTIZ8?si=IrMZcxFNUiWuFgcP",
        "Đứng tấn nâng bắp chân dựa tường" to "https://youtu.be/Hcy81KUTIZ8?si=IrMZcxFNUiWuFgcP",

        // Cơ bụng
        "abs_001" to "https://youtu.be/8lsAXzvVHrc?si=Aig4uaX6mWfy9_Zo",
        "Gập người xe đạp đứng" to "https://youtu.be/8lsAXzvVHrc?si=Aig4uaX6mWfy9_Zo",

        "abs_002" to "https://youtu.be/DJQGX2J4IVw?si=7Emzld1D0uf6oVDY",
        "Gập bụng chéo kiểu nga" to "https://youtu.be/DJQGX2J4IVw?si=7Emzld1D0uf6oVDY",

        "abs_003" to "https://youtu.be/RUNrHkbP4Pc?si=ZU5aOoIOfw1Woqml",
        "Tập cơ bụng" to "https://youtu.be/RUNrHkbP4Pc?si=ZU5aOoIOfw1Woqml",

        "abs_004" to "https://youtu.be/UwRfRN5fYRg?si=DF_J7GtVXwUgYKUY",
        "Gập bụng ngược" to "https://youtu.be/UwRfRN5fYRg?si=DF_J7GtVXwUgYKUY",

        "abs_005" to "https://youtu.be/9bR-elyolBQ?si=7oDX3SQivCfs2R1u",
        "Chạm gót chân" to "https://youtu.be/9bR-elyolBQ?si=7oDX3SQivCfs2R1u",

        "abs_006" to "https://youtu.be/bXMQkRowNk8?si=9c8yvvCZXPBUtj7n",
        "Con bọ" to "https://youtu.be/bXMQkRowNk8?si=9c8yvvCZXPBUtj7n",

        "abs_007" to "https://youtu.be/Fcbw82ykBvY?si=8KilotkIu-MbmJwF",
        "Đo sàn" to "https://youtu.be/Fcbw82ykBvY?si=8KilotkIu-MbmJwF",

        "abs_008" to "https://youtu.be/R4hV4xrJNqc?si=kMPt50lls_p_sUNr",
        "Hai đầu gối chạm ngực" to "https://youtu.be/R4hV4xrJNqc?si=kMPt50lls_p_sUNr",

        "abs_009" to "https://youtu.be/ZI-j_POtzlU?si=UYvPgZ5iWHGpzE2L",
        "Nằm vặn người trái" to "https://youtu.be/ZI-j_POtzlU?si=UYvPgZ5iWHGpzE2L",

        "abs_010" to "https://youtu.be/ZI-j_POtzlU?si=_XwJQ-9tYVc17vhH",
        "Nằm vặn người phải" to "https://youtu.be/ZI-j_POtzlU?si=_XwJQ-9tYVc17vhH",

        "abs_011" to "https://youtu.be/IqU06UsPp1k?si=UvmEKAol0TXTj-UI",
        "Gập người gối chạm khuỷu tay" to "https://youtu.be/IqU06UsPp1k?si=UvmEKAol0TXTj-UI",

        "abs_012" to "https://youtu.be/GxKoSEkmRC8?si=Ge8tzAuHgCfeRHKc",
        "Nâng tay dài" to "https://youtu.be/GxKoSEkmRC8?si=Ge8tzAuHgCfeRHKc",

        "abs_013" to "https://youtu.be/XKW5jru5pGo?si=p0YtAfgL6MtLaPBh",
        "Nghiên người vặn cơ liên sườn" to "https://youtu.be/XKW5jru5pGo?si=p0YtAfgL6MtLaPBh",
        "Nghiêng người vặn cơ liên sườn" to "https://youtu.be/XKW5jru5pGo?si=p0YtAfgL6MtLaPBh",

        "abs_014" to "https://youtu.be/dGKbTKLnym4?si=FwY7gmAYLm0fCI1b",
        "Nâng chân" to "https://youtu.be/dGKbTKLnym4?si=FwY7gmAYLm0fCI1b",

        "abs_015" to "https://youtu.be/yCVyaX-RjLM?si=nQEPmvDIeJZGErOe",
        "Tấm ván đi bộ ngang" to "https://youtu.be/yCVyaX-RjLM?si=nQEPmvDIeJZGErOe",

        "abs_016" to "https://youtu.be/-nJkAJpQemI?si=SsKrSnIgiaxrjloL",
        "Gập bụng đạp xe" to "https://youtu.be/-nJkAJpQemI?si=SsKrSnIgiaxrjloL",

        "abs_017" to "https://youtu.be/Qz3ylqqJ90M?si=_nHojJ_gqyMtniK9",
        "Gập bụng ngang thân" to "https://youtu.be/Qz3ylqqJ90M?si=_nHojJ_gqyMtniK9",

        "abs_018" to "https://youtu.be/O9j5_BriCW4?si=fCbexm54-5J7Iqve",
        "Tung chân hít đất" to "https://youtu.be/O9j5_BriCW4?si=fCbexm54-5J7Iqve",

        "abs_019" to "https://youtu.be/wD2GY3fUJqQ?si=A--9j5IGFo5ho_Ha",
        "Đứng gập cơ liên sườn trái" to "https://youtu.be/wD2GY3fUJqQ?si=A--9j5IGFo5ho_Ha",

        "abs_020" to "https://youtu.be/wD2GY3fUJqQ?si=16fEohFS3gqwz8i7",
        "Đứng gập cơ liên sườn phải" to "https://youtu.be/wD2GY3fUJqQ?si=16fEohFS3gqwz8i7",

        "abs_021" to "https://youtu.be/CoTLqNsivCI?si=kuIemoaSG_hFL6wC",
        "Gập người sao biển" to "https://youtu.be/CoTLqNsivCI?si=kuIemoaSG_hFL6wC"
    )

    // Chức năng: lấy link YouTube theo thứ tự ưu tiên:
    // 1. Link có sẵn trong JSON.
    // 2. Link theo id bài tập.
    // 3. Link theo tên bài tập.
    fun getYoutubeUrl(
        exerciseId: String,
        exerciseName: String,
        defaultUrl: String
    ): String {
        if (defaultUrl.isNotBlank()) {
            return defaultUrl
        }

        return youtubeLinks[exerciseId] ?: youtubeLinks[exerciseName] ?: ""
    }
}