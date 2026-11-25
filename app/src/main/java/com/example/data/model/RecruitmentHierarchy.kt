package com.example.data.model

enum class GovOrgGroup {
  CENTRAL_GOVERNMENT,
  OTHER_GOVERNMENT,
}

data class GovOrganisationNode(
  val id: String,
  val shortName: String,
  val fullName: String,
  val subtitle: String,
  val group: GovOrgGroup,
  val subCategories: List<RecruitmentSubCategory>,
)

data class RecruitmentSubCategory(
  val id: String,
  val title: String,
  val subtitle: String,
)

enum class StateQualificationTier(val label: String) {
  ALL("All Levels"),
  DEGREE("Degree Level"),
  PLUS_TWO("Plus Two Level"),
  SSLC("SSLC Level"),
  OTHER("Other"),
}

data class PrivateDomainNode(
  val id: String,
  val title: String,
  val subtitle: String,
)

data class PrivateCompanyNode(
  val id: String,
  val name: String,
  val tagline: String,
  val headquarters: String,
  val careerPortalUrl: String,
  val domainIds: Set<String>,
  val subCategories: List<RecruitmentSubCategory>,
)

object RecruitmentHierarchy {

  val govOrganisations: List<GovOrganisationNode> =
    listOf(
      GovOrganisationNode(
        id = "SSC",
        shortName = "SSC",
        fullName = "Staff Selection Commission",
        subtitle = "CGL, CHSL (10+2), MTS & CPWD Junior Engineer",
        group = GovOrgGroup.CENTRAL_GOVERNMENT,
        subCategories =
          listOf(
            RecruitmentSubCategory("ALL", "All Notifications", "All SSC open recruitments"),
            RecruitmentSubCategory("CGL", "CGL Graduate", "Combined Graduate Level cadres"),
            RecruitmentSubCategory("CHSL", "CHSL (10+2)", "LDC, JSA & Data Entry Operator"),
            RecruitmentSubCategory("MTS", "MTS & Havaldar", "Matriculation 10th pass posts"),
            RecruitmentSubCategory("JE", "Junior Engineer", "Civil, Electrical & Mechanical"),
          ),
      ),
      GovOrganisationNode(
        id = "RRB",
        shortName = "RRB",
        fullName = "Railway Recruitment Board",
        subtitle = "NTPC Graduate, Station Master, JE & Technician",
        group = GovOrgGroup.CENTRAL_GOVERNMENT,
        subCategories =
          listOf(
            RecruitmentSubCategory("ALL", "All Notifications", "All Railway zone recruitments"),
            RecruitmentSubCategory("NTPC_GRAD", "NTPC Graduate", "Goods Train Manager & Traffic"),
            RecruitmentSubCategory("NTPC_UG", "NTPC Undergraduate", "12th pass commercial & clerk"),
            RecruitmentSubCategory("STATION_MASTER", "Station Master", "Level-6 operating cadre"),
            RecruitmentSubCategory("TECHNICIAN", "Technician", "Signal, Telecom & ITI trades"),
            RecruitmentSubCategory("JE", "Junior Engineer", "ECE, Electrical & Mechanical JE"),
            RecruitmentSubCategory("OTHER", "Other RRB", "RPF, Paramedical & Ministerial"),
          ),
      ),
      GovOrganisationNode(
        id = "IBPS",
        shortName = "IBPS",
        fullName = "Institute of Banking Personnel Selection",
        subtitle = "Public Sector Bank PO, Clerk & Specialist Officer",
        group = GovOrgGroup.CENTRAL_GOVERNMENT,
        subCategories =
          listOf(
            RecruitmentSubCategory("ALL", "All Notifications", "All IBPS & Banking open drives"),
            RecruitmentSubCategory("PO_MT", "PO / Management Trainee", "11 Participating Public Banks"),
            RecruitmentSubCategory("SBI_REG", "SBI & Regulatory", "SBI PO & SEBI Grade 'A'"),
          ),
      ),
      GovOrganisationNode(
        id = "IB",
        shortName = "IB",
        fullName = "Intelligence Bureau (MHA)",
        subtitle = "ACIO Grade-II / Executive & Security Assistant",
        group = GovOrgGroup.CENTRAL_GOVERNMENT,
        subCategories =
          listOf(
            RecruitmentSubCategory("ALL", "All Notifications", "MHA & Intelligence Bureau drives"),
            RecruitmentSubCategory("ACIO", "ACIO Grade-II / Exec", "Graduate intelligence cadres"),
          ),
      ),
      GovOrganisationNode(
        id = "DRDO",
        shortName = "DRDO",
        fullName = "Defence R&D Organisation",
        subtitle = "Scientist 'B' (RAC) & CEPTAM Technical Cadres",
        group = GovOrgGroup.CENTRAL_GOVERNMENT,
        subCategories =
          listOf(
            RecruitmentSubCategory("ALL", "All Notifications", "All DRDO active notifications"),
            RecruitmentSubCategory("SCIENTIST", "Scientist 'B'", "ECE, CSE & Mechanical GATE"),
          ),
      ),
      GovOrganisationNode(
        id = "ISRO",
        shortName = "ISRO",
        fullName = "Indian Space Research Organisation",
        subtitle = "Scientist / Engineer 'SC' & Technical Assistant",
        group = GovOrgGroup.CENTRAL_GOVERNMENT,
        subCategories =
          listOf(
            RecruitmentSubCategory("ALL", "All Notifications", "VSSC, URSC, SAC & LPSC drives"),
            RecruitmentSubCategory("SCIENTIST_SC", "Scientist / Engg 'SC'", "B.Tech Electronics & Mech"),
            RecruitmentSubCategory("TECH_ASST", "Technical Assistant", "Polytechnic Diploma cadres"),
          ),
      ),
      GovOrganisationNode(
        id = "UPSC",
        shortName = "UPSC",
        fullName = "Union Public Service Commission",
        subtitle = "Engineering Services (ESE/IES), CSE & CDS",
        group = GovOrgGroup.CENTRAL_GOVERNMENT,
        subCategories =
          listOf(
            RecruitmentSubCategory("ALL", "All Notifications", "UPSC national examinations"),
            RecruitmentSubCategory("ESE", "Engineering Services (ESE)", "Group-A engineering cadres"),
          ),
      ),
      GovOrganisationNode(
        id = "OTHER_CENTRAL",
        shortName = "Other Central Govt",
        fullName = "Other Central Ministries & Regulators",
        subtitle = "SEBI, RBI, CPWD & Central Autonomous Wings",
        group = GovOrgGroup.CENTRAL_GOVERNMENT,
        subCategories =
          listOf(
            RecruitmentSubCategory("ALL", "All Notifications", "Central regulatory & ministry posts"),
            RecruitmentSubCategory("REGULATORY", "SEBI & Regulators", "Grade A & Specialist officers"),
          ),
      ),
      // Other Government Organisations
      GovOrganisationNode(
        id = "PSUS",
        shortName = "PSUs",
        fullName = "Maharatna & Navratna PSUs",
        subtitle = "NTPC, POWERGRID, BHEL & GAIL Executive Trainees",
        group = GovOrgGroup.OTHER_GOVERNMENT,
        subCategories =
          listOf(
            RecruitmentSubCategory("ALL", "All Notifications", "All PSU Engineering Trainees"),
            RecruitmentSubCategory("GATE_ET", "Executive Trainee (GATE)", "Electrical, ECE & Mech"),
          ),
      ),
      GovOrganisationNode(
        id = "RESEARCH",
        shortName = "Research Organisations",
        fullName = "National Research Labs (CSIR / BARC / C-DOT)",
        subtitle = "Scientific Officers, JRF & R&D Fellowships",
        group = GovOrgGroup.OTHER_GOVERNMENT,
        subCategories =
          listOf(
            RecruitmentSubCategory("ALL", "All Notifications", "R&D & Space/Defence labs"),
            RecruitmentSubCategory("SCIENTIST", "Scientist & R&D", "DRDO & ISRO R&D centers"),
          ),
      ),
      GovOrganisationNode(
        id = "INSTITUTIONS",
        shortName = "Government Institutions",
        fullName = "IITs, NITs, AIIMS & Central Universities",
        subtitle = "Non-teaching Technical, Admin & Registrar posts",
        group = GovOrgGroup.OTHER_GOVERNMENT,
        subCategories =
          listOf(
            RecruitmentSubCategory("ALL", "All Notifications", "Central academic & tech institutes"),
          ),
      ),
      GovOrganisationNode(
        id = "OTHER_BODIES",
        shortName = "Other Central Bodies",
        fullName = "Statutory Commissions & Tribunals",
        subtitle = "SEBI, Courts, Election & Financial Regulators",
        group = GovOrgGroup.OTHER_GOVERNMENT,
        subCategories =
          listOf(
            RecruitmentSubCategory("ALL", "All Notifications", "Statutory & regulatory commissions"),
          ),
      ),
    )

  val stateSubsections: List<RecruitmentSubCategory> =
    listOf(
      RecruitmentSubCategory("PSC", "Public Service Commission", "State civil, executive & combined exams"),
      RecruitmentSubCategory("DEPTS", "State Departments", "Electricity Boards, Police & Transport"),
      RecruitmentSubCategory("UNIV_BOARDS", "Universities & Boards", "High Courts, Tech Boards & Universities"),
      RecruitmentSubCategory("STATE_LEVEL", "State-Level Recruitment", "All state-level notifications"),
    )

  val privateDomains: List<PrivateDomainNode> =
    listOf(
      PrivateDomainNode("IT_SOFTWARE", "IT & Software", "Product SDE, Cloud & Off-Campus Drives"),
      PrivateDomainNode(
        "CORE_ENGG",
        "Core Engineering (ECE/EEE/Mech/Civil)",
        "Powertrain, Aerospace & Industrial",
      ),
      PrivateDomainNode(
        "ELECTRONICS_EMBEDDED",
        "Electronics & Embedded",
        "VLSI, Firmware, Silicon & Verification",
      ),
      PrivateDomainNode("RND", "R&D", "Labs, Analog Circuits & Semiconductor R&D"),
      PrivateDomainNode("MANUFACTURING", "Manufacturing", "Semiconductor Fab, EV & Plant Ops"),
      PrivateDomainNode("GRAD_TRAINEE", "Graduate Trainee", "2025–2026 New Grad & GET Intake"),
      PrivateDomainNode(
        "INTERNSHIP",
        "Internship & Apprenticeship",
        "Early-career & University Programs",
      ),
      PrivateDomainNode("OTHER_PRIVATE", "Other Private", "Fintech Ops, Analytics & Remote"),
    )

  val privateCompanies: List<PrivateCompanyNode> =
    listOf(
      PrivateCompanyNode(
        id = "TCS",
        name = "TCS",
        tagline = "Tata Consultancy Services • Pan India",
        headquarters = "Mumbai / Pan India",
        careerPortalUrl = "https://nextstep.tcs.com",
        domainIds = setOf("IT_SOFTWARE", "GRAD_TRAINEE"),
        subCategories =
          listOf(
            RecruitmentSubCategory("ALL", "All Openings", "Active TCS hiring drives"),
            RecruitmentSubCategory("OFF_CAMPUS", "NQT Off-Campus", "Ninja, Digital & Prime"),
          ),
      ),
      PrivateCompanyNode(
        id = "ZOHO",
        name = "Zoho",
        tagline = "Zoho Corporation • Chennai & Remote",
        headquarters = "Chennai / Tenkasi",
        careerPortalUrl = "https://www.zoho.com/careers/",
        domainIds = setOf("IT_SOFTWARE"),
        subCategories =
          listOf(
            RecruitmentSubCategory("ALL", "All Openings", "Active Zoho software openings"),
          ),
      ),
      PrivateCompanyNode(
        id = "QUALCOMM",
        name = "Qualcomm India",
        tagline = "Embedded Firmware & VLSI Systems",
        headquarters = "Bengaluru / Hyderabad",
        careerPortalUrl = "https://careers.qualcomm.com",
        domainIds = setOf("CORE_ENGG", "ELECTRONICS_EMBEDDED", "RND"),
        subCategories =
          listOf(
            RecruitmentSubCategory("ALL", "All Openings", "Qualcomm ECE & Firmware roles"),
          ),
      ),
      PrivateCompanyNode(
        id = "BOSCH",
        name = "Bosch",
        tagline = "Bosch Global Software & Mobility",
        headquarters = "Bengaluru / Coimbatore",
        careerPortalUrl = "https://www.bosch.in/careers/",
        domainIds = setOf("CORE_ENGG", "ELECTRONICS_EMBEDDED", "GRAD_TRAINEE"),
        subCategories =
          listOf(
            RecruitmentSubCategory("ALL", "All Openings", "Bosch GET & Embedded openings"),
          ),
      ),
      PrivateCompanyNode(
        id = "TI",
        name = "Texas Instruments",
        tagline = "Analog & Embedded Semiconductor R&D",
        headquarters = "Bengaluru, KA",
        careerPortalUrl = "https://careers.ti.com",
        domainIds = setOf("ELECTRONICS_EMBEDDED", "RND", "CORE_ENGG"),
        subCategories =
          listOf(
            RecruitmentSubCategory("ALL", "All Openings", "Texas Instruments India roles"),
          ),
      ),
      PrivateCompanyNode(
        id = "FLIPKART",
        name = "Flipkart",
        tagline = "Bengaluru Tech Hub • SDE & GRiD",
        headquarters = "Bengaluru, KA",
        careerPortalUrl = "https://www.flipkartcareers.com",
        domainIds = setOf("IT_SOFTWARE", "GRAD_TRAINEE"),
        subCategories =
          listOf(
            RecruitmentSubCategory("ALL", "All Openings", "Flipkart Engineering & GRiD"),
          ),
      ),
      PrivateCompanyNode(
        id = "ATLASSIAN",
        name = "Atlassian",
        tagline = "Distributed Engineering • Remote India",
        headquarters = "Remote / Bengaluru",
        careerPortalUrl = "https://www.atlassian.com/company/careers",
        domainIds = setOf("IT_SOFTWARE", "OTHER_PRIVATE"),
        subCategories =
          listOf(
            RecruitmentSubCategory("ALL", "All Openings", "Remote New Grad & P30 roles"),
          ),
      ),
      PrivateCompanyNode(
        id = "TATA_TECH",
        name = "Tata Technologies",
        tagline = "AUTOSAR Embedded & EV Design",
        headquarters = "Pune, MH",
        careerPortalUrl = "https://www.tatatechnologies.com/in/careers/",
        domainIds = setOf("CORE_ENGG", "ELECTRONICS_EMBEDDED", "MANUFACTURING"),
        subCategories =
          listOf(
            RecruitmentSubCategory("ALL", "All Openings", "Pune Embedded & Mechanical roles"),
          ),
      ),
      PrivateCompanyNode(
        id = "TATA_ELEC",
        name = "Tata Electronics",
        tagline = "Semiconductor Fab • Dholera / Sanand",
        headquarters = "Ahmedabad / Dholera, GJ",
        careerPortalUrl = "https://www.tataelectronics.com/careers",
        domainIds = setOf("MANUFACTURING", "ELECTRONICS_EMBEDDED", "GRAD_TRAINEE"),
        subCategories =
          listOf(
            RecruitmentSubCategory("ALL", "All Openings", "Semiconductor Fab GET & Diploma"),
          ),
      ),
      PrivateCompanyNode(
        id = "MICRON",
        name = "Micron Technology",
        tagline = "DRAM / NAND Verification & Test",
        headquarters = "Hyderabad, TS",
        careerPortalUrl = "https://careers.micron.com",
        domainIds = setOf("ELECTRONICS_EMBEDDED", "RND"),
        subCategories =
          listOf(
            RecruitmentSubCategory("ALL", "All Openings", "Memory Verification & Test"),
          ),
      ),
      PrivateCompanyNode(
        id = "UST",
        name = "UST Global",
        tagline = "Technopark Cloud & Software Drive",
        headquarters = "TVM / Kochi, KL",
        careerPortalUrl = "https://www.ust.com/en/careers",
        domainIds = setOf("IT_SOFTWARE", "GRAD_TRAINEE"),
        subCategories =
          listOf(
            RecruitmentSubCategory("ALL", "All Openings", "Kerala Technopark drives"),
          ),
      ),
      PrivateCompanyNode(
        id = "IBS",
        name = "IBS Software",
        tagline = "Aviation & Travel SaaS • Infopark",
        headquarters = "Kochi, KL",
        careerPortalUrl = "https://www.ibsplc.com/careers",
        domainIds = setOf("IT_SOFTWARE"),
        subCategories =
          listOf(
            RecruitmentSubCategory("ALL", "All Openings", "Kochi Infopark openings"),
          ),
      ),
      PrivateCompanyNode(
        id = "ASHOK_LEYLAND",
        name = "Ashok Leyland",
        tagline = "EV Powertrain & Commercial Manufacturing",
        headquarters = "Chennai / Hosur, TN",
        careerPortalUrl = "https://www.ashokleyland.com/in/en/careers",
        domainIds = setOf("CORE_ENGG", "MANUFACTURING", "GRAD_TRAINEE"),
        subCategories =
          listOf(
            RecruitmentSubCategory("ALL", "All Openings", "Plant & EV Powertrain GET"),
          ),
      ),
      PrivateCompanyNode(
        id = "RAZORPAY",
        name = "Razorpay",
        tagline = "Fintech Solutions & WFH Operations",
        headquarters = "Remote / Bengaluru",
        careerPortalUrl = "https://razorpay.com/jobs/",
        domainIds = setOf("OTHER_PRIVATE", "IT_SOFTWARE"),
        subCategories =
          listOf(
            RecruitmentSubCategory("ALL", "All Openings", "Fintech Engineering & Ops"),
          ),
      ),
      PrivateCompanyNode(
        id = "ZOMATO",
        name = "Zomato & Blinkit",
        tagline = "Consumer Tech & Quick-Commerce Ops",
        headquarters = "Gurugram / Delhi NCR",
        careerPortalUrl = "https://www.zomato.com/careers",
        domainIds = setOf("IT_SOFTWARE", "OTHER_PRIVATE"),
        subCategories =
          listOf(
            RecruitmentSubCategory("ALL", "All Openings", "NCR Tech & Product Ops"),
          ),
      ),
      PrivateCompanyNode(
        id = "GOLDMAN",
        name = "Goldman Sachs",
        tagline = "Quantitative Risk & Finance Operations",
        headquarters = "Bengaluru / Hyderabad",
        careerPortalUrl = "https://www.goldmansachs.com/careers/",
        domainIds = setOf("OTHER_PRIVATE", "INTERNSHIP"),
        subCategories =
          listOf(
            RecruitmentSubCategory("ALL", "All Openings", "Analyst & Quant drives"),
          ),
      ),
    )

  fun findGovOrg(orgId: String): GovOrganisationNode? =
    govOrganisations.firstOrNull { it.id.equals(orgId, ignoreCase = true) }

  fun findPrivateDomain(domainId: String): PrivateDomainNode? =
    privateDomains.firstOrNull { it.id.equals(domainId, ignoreCase = true) }

  fun findPrivateCompany(companyId: String): PrivateCompanyNode? =
    privateCompanies.firstOrNull { it.id.equals(companyId, ignoreCase = true) }

  fun matchesGovOrg(job: JobAlert, orgId: String): Boolean {
    if (job.sector != JobSector.CENTRAL_GOVT) return false
    val tag = job.orgTag?.uppercase()
    if (tag == orgId.uppercase()) return true

    val orgLower = job.organization.lowercase()
    val domainLower = job.officialDomain.lowercase()
    return when (orgId.uppercase()) {
      "SSC" -> job.centralAgency == CentralAgencyFilter.SSC || "ssc.gov.in" in domainLower || "ssc" in orgLower
      "RRB" -> job.centralAgency == CentralAgencyFilter.RRB || "rrb" in domainLower || "rrb" in orgLower
      "IBPS" -> job.centralAgency == CentralAgencyFilter.BANKING && ("ibps" in orgLower || "ibps" in domainLower || "sbi" in orgLower)
      "IB" -> job.centralAgency == CentralAgencyFilter.MHA_IB || "mha.gov.in" in domainLower || "intelligence bureau" in orgLower
      "DRDO" -> "drdo" in domainLower || "rac.gov.in" in domainLower || "drdo" in orgLower
      "ISRO" -> "isro" in domainLower || "isro" in orgLower
      "UPSC" -> job.centralAgency == CentralAgencyFilter.UPSC || "upsc" in domainLower || "upsc" in orgLower
      "OTHER_CENTRAL", "OTHER_BODIES" -> "sebi" in domainLower || "rbi" in domainLower || "sebi" in orgLower
      "PSUS" -> job.centralAgency == CentralAgencyFilter.PSUS || "ntpc" in orgLower || "powergrid" in orgLower
      "RESEARCH" -> job.centralAgency == CentralAgencyFilter.DRDO_ISRO || "csir" in orgLower || "barc" in orgLower
      "INSTITUTIONS" -> "iit" in orgLower || "nit" in orgLower || "aiims" in orgLower
      else -> false
    }
  }

  fun matchesOrgSubCategory(job: JobAlert, orgId: String, categoryId: String): Boolean {
    if (!matchesGovOrg(job, orgId)) return false
    if (categoryId == "ALL") return true
    if (job.subCategoryTag.equals(categoryId, ignoreCase = true)) return true

    val title = job.postTitle.lowercase()
    return when (orgId.uppercase()) {
      "RRB" ->
        when (categoryId) {
          "NTPC_GRAD" -> "ntpc" in title && ("graduate" in title || "station master" in title || "goods" in title)
          "NTPC_UG" -> "ntpc" in title && ("undergraduate" in title || "12th" in title)
          "STATION_MASTER" -> "station master" in title
          "TECHNICIAN" -> "technician" in title
          "JE" -> "junior engineer" in title || " je " in title
          "OTHER" -> "rpf" in title || "paramedical" in title || "ministerial" in title
          else -> false
        }
      "SSC" ->
        when (categoryId) {
          "CGL" -> "cgl" in title || "combined graduate" in title
          "CHSL" -> "chsl" in title || "10+2" in title
          "MTS" -> "mts" in title || "multi-tasking" in title || "havaldar" in title
          "JE" -> "junior engineer" in title || " je " in title
          else -> false
        }
      "IBPS" ->
        when (categoryId) {
          "PO_MT" -> "po" in title || "probationary" in title || "mt" in title
          "SBI_REG" -> "sbi" in job.organization.lowercase() || "sebi" in job.organization.lowercase()
          else -> false
        }
      "ISRO" ->
        when (categoryId) {
          "SCIENTIST_SC" -> "scientist" in title || "engineer" in title
          "TECH_ASST" -> "technical assistant" in title || "diploma" in title
          else -> false
        }
      else -> true
    }
  }

  fun resolveStateSubsection(job: JobAlert): String {
    if (!job.subCategoryTag.isNullOrBlank()) return job.subCategoryTag
    val org = job.organization.lowercase()
    return when {
      "high court" in org || "university" in org || "board" in org -> "UNIV_BOARDS"
      "electricity" in org || "kseb" in org || "tangedco" in org || "kptcl" in org ||
        "police" in org || "msrtc" in org -> "DEPTS"
      "psc" in org || "dsssb" in org -> "PSC"
      else -> "STATE_LEVEL"
    }
  }

  fun resolveStateQualTier(job: JobAlert): StateQualificationTier {
    return when {
      job.minGeneralQualification == QualificationLevel.TENTH -> StateQualificationTier.SSLC
      job.minGeneralQualification == QualificationLevel.TWELFTH -> StateQualificationTier.PLUS_TWO
      job.minGeneralQualification == QualificationLevel.GRADUATE ||
        job.minGeneralQualification == QualificationLevel.BTECH ||
        job.minGeneralQualification == QualificationLevel.POSTGRADUATE -> StateQualificationTier.DEGREE
      else -> StateQualificationTier.OTHER
    }
  }

  fun matchesCompany(job: JobAlert, companyId: String): Boolean {
    if (job.orgTag.equals(companyId, ignoreCase = true)) return true
    val company = findPrivateCompany(companyId) ?: return false
    val orgLower = job.organization.lowercase()
    val domainLower = job.officialDomain.lowercase()
    val portalHost = company.careerPortalUrl.substringAfter("https://").substringBefore("/").lowercase()
    return company.name.lowercase() in orgLower ||
      (domainLower.isNotBlank() && (domainLower in portalHost || portalHost in domainLower))
  }
}
