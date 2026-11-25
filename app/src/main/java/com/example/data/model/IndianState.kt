package com.example.data.model

enum class RegionType(val label: String) {
  STATE("STATE"),
  UT("UT"),
}

data class IndianState(
  val code: String,
  val name: String,
  val type: RegionType,
  val primaryHub: String,
  val boardShort: String,
)

object IndianStatesCatalog {
  val allRegions: List<IndianState> =
    listOf(
      // 28 States
      IndianState("AP", "Andhra Pradesh", RegionType.STATE, "Amaravati / Vizag", "APPSC"),
      IndianState("AR", "Arunachal Pradesh", RegionType.STATE, "Itanagar", "APPSC"),
      IndianState("AS", "Assam", RegionType.STATE, "Guwahati", "APSC"),
      IndianState("BR", "Bihar", RegionType.STATE, "Patna", "BPSC"),
      IndianState("CG", "Chhattisgarh", RegionType.STATE, "Raipur", "CGPSC"),
      IndianState("GA", "Goa", RegionType.STATE, "Panaji", "GPSC"),
      IndianState("GJ", "Gujarat", RegionType.STATE, "Gandhinagar / GIFT", "GPSC"),
      IndianState("HR", "Haryana", RegionType.STATE, "Gurugram / Panchkula", "HPSC"),
      IndianState("HP", "Himachal Pradesh", RegionType.STATE, "Shimla", "HPPSC"),
      IndianState("JH", "Jharkhand", RegionType.STATE, "Ranchi", "JPSC"),
      IndianState("KA", "Karnataka", RegionType.STATE, "Bengaluru", "KPSC"),
      IndianState("KL", "Kerala", RegionType.STATE, "TVM / Kochi", "Kerala PSC"),
      IndianState("MP", "Madhya Pradesh", RegionType.STATE, "Indore / Bhopal", "MPPSC"),
      IndianState("MH", "Maharashtra", RegionType.STATE, "Mumbai / Pune", "MPSC"),
      IndianState("MN", "Manipur", RegionType.STATE, "Imphal", "MPSC"),
      IndianState("ML", "Meghalaya", RegionType.STATE, "Shillong", "MPSC"),
      IndianState("MZ", "Mizoram", RegionType.STATE, "Aizawl", "MPSC"),
      IndianState("NL", "Nagaland", RegionType.STATE, "Kohima", "NPSC"),
      IndianState("OD", "Odisha", RegionType.STATE, "Bhubaneswar", "OPSC"),
      IndianState("PB", "Punjab", RegionType.STATE, "Mohali / Chandigarh", "PPSC"),
      IndianState("RJ", "Rajasthan", RegionType.STATE, "Jaipur", "RPSC"),
      IndianState("SK", "Sikkim", RegionType.STATE, "Gangtok", "SPSC"),
      IndianState("TN", "Tamil Nadu", RegionType.STATE, "Chennai", "TNPSC"),
      IndianState("TS", "Telangana", RegionType.STATE, "Hyderabad", "TGPSC"),
      IndianState("TR", "Tripura", RegionType.STATE, "Agartala", "TPSC"),
      IndianState("UP", "Uttar Pradesh", RegionType.STATE, "Lucknow / Noida", "UPPSC"),
      IndianState("UK", "Uttarakhand", RegionType.STATE, "Dehradun", "UKPSC"),
      IndianState("WB", "West Bengal", RegionType.STATE, "Kolkata", "WBPSC"),
      // 8 Union Territories
      IndianState("AN", "Andaman & Nicobar", RegionType.UT, "Sri Vijaya Puram", "A&N Admin"),
      IndianState("CH", "Chandigarh", RegionType.UT, "Chandigarh", "UT Admin"),
      IndianState("DN", "Dadra & Daman", RegionType.UT, "Daman", "UT Admin"),
      IndianState("DL", "Delhi (NCT)", RegionType.UT, "New Delhi", "DSSSB"),
      IndianState("JK", "Jammu & Kashmir", RegionType.UT, "Srinagar / Jammu", "JKPSC"),
      IndianState("LA", "Ladakh", RegionType.UT, "Leh", "LAHDC"),
      IndianState("LD", "Lakshadweep", RegionType.UT, "Kavaratti", "UT Admin"),
      IndianState("PY", "Puducherry", RegionType.UT, "Puducherry", "UT Admin"),
    )

  fun findByCode(code: String): IndianState? =
    allRegions.firstOrNull { it.code.equals(code, ignoreCase = true) }
}
