package com.example.data.model

import java.net.URI

sealed class SourceVerificationResult {
  abstract val isVerified: Boolean

  data class Verified(
    val domain: String,
    val verifiedTimestamp: String,
  ) : SourceVerificationResult() {
    override val isVerified: Boolean = true
  }

  data class Blocked(val reason: String) : SourceVerificationResult() {
    override val isVerified: Boolean = false
  }
}

object OfficialSourceVerifier {

  private val blockedAggregatorKeywords =
    setOf(
      "sarkariresult",
      "freejobalert",
      "freshersnow",
      "jobriya",
      "sarkarinaukri",
      "blogspot",
      "wordpress",
      "wixsite",
      "bit.ly",
      "tinyurl",
    )

  private val officialGovSuffixes =
    listOf(".gov.in", ".nic.in", ".org.in", ".ac.in", ".res.in", ".edu.in")

  private val officialStatutoryGovHosts =
    setOf(
      "ibps.in",
      "www.ibps.in",
      "sbi.co.in",
      "www.sbi.co.in",
      "careers.ntpc.co.in",
      "keralacourts.in",
      "hckrecruitment.keralacourts.in",
      "tnpscexams.in",
      "apply.tnpscexams.in",
      "mahait.org",
      "apscrecruitment.in",
    )

  private val verifiedCorporateHosts =
    setOf(
      "tcs.com",
      "www.tcs.com",
      "careers.tcs.com",
      "nextstep.tcs.com",
      "zoho.com",
      "www.zoho.com",
      "qualcomm.com",
      "careers.qualcomm.com",
      "bosch.in",
      "www.bosch.in",
      "ti.com",
      "careers.ti.com",
      "flipkartcareers.com",
      "www.flipkartcareers.com",
      "atlassian.com",
      "www.atlassian.com",
      "tatatechnologies.com",
      "www.tatatechnologies.com",
      "tataelectronics.com",
      "www.tataelectronics.com",
      "micron.com",
      "careers.micron.com",
      "ust.com",
      "www.ust.com",
      "ibsplc.com",
      "www.ibsplc.com",
      "ashokleyland.com",
      "www.ashokleyland.com",
      "razorpay.com",
      "zomato.com",
      "www.zomato.com",
      "goldmansachs.com",
      "www.goldmansachs.com",
    )

  fun verifyJob(job: JobAlert): SourceVerificationResult {
    return verifySourceFields(
      officialDomain = job.officialDomain,
      lastVerifiedTimestamp = job.lastVerifiedTimestamp,
      primaryUrl = job.directApplyUrl,
      secondaryUrl = job.officialPdfUrl,
      isGovSector =
        job.sector == JobSector.CENTRAL_GOVT || job.sector == JobSector.STATE_GOVT,
    )
  }

  fun verifyExamUpdate(update: ExamUpdate): SourceVerificationResult {
    return verifySourceFields(
      officialDomain = update.officialDomain,
      lastVerifiedTimestamp = update.lastVerifiedTimestamp,
      primaryUrl = update.portalLoginUrl,
      secondaryUrl = update.noticePdfUrl,
      isGovSector = !isCorporateHost(extractHost(update.portalLoginUrl).orEmpty()),
    )
  }

  fun isValidOfficialDomain(domain: String, isGovSector: Boolean): Boolean {
    val cleanHost = domain.trim().lowercase().substringBefore("/")
    if (cleanHost.isEmpty()) return false
    if (blockedAggregatorKeywords.any { it in cleanHost }) return false
    return if (isGovSector) {
      isOfficialGovHost(cleanHost)
    } else {
      isOfficialGovHost(cleanHost) || isCorporateHost(cleanHost)
    }
  }

  private fun verifySourceFields(
    officialDomain: String,
    lastVerifiedTimestamp: String,
    primaryUrl: String,
    secondaryUrl: String?,
    isGovSector: Boolean,
  ): SourceVerificationResult {
    val cleanDomain = officialDomain.trim().lowercase()
    if (cleanDomain.isBlank()) {
      return SourceVerificationResult.Blocked("Missing officialDomain")
    }
    if (lastVerifiedTimestamp.isBlank()) {
      return SourceVerificationResult.Blocked("Missing lastVerifiedTimestamp")
    }

    val primaryHost = extractHttpsHost(primaryUrl)
      ?: return SourceVerificationResult.Blocked("Primary URL must be valid HTTPS")
    if (blockedAggregatorKeywords.any { it in primaryHost || it in cleanDomain }) {
      return SourceVerificationResult.Blocked("Third-party aggregator URLs are blocked")
    }

    val domainHost = cleanDomain.substringBefore("/")
    if (!hostsAlign(domainHost, primaryHost)) {
      return SourceVerificationResult.Blocked("officialDomain does not match portal URL host")
    }

    if (!isValidOfficialDomain(cleanDomain, isGovSector)) {
      return SourceVerificationResult.Blocked("Unverified domain: $cleanDomain")
    }

    if (!secondaryUrl.isNullOrBlank()) {
      val secondaryHost = extractHttpsHost(secondaryUrl)
        ?: return SourceVerificationResult.Blocked("Secondary PDF URL must be valid HTTPS")
      if (blockedAggregatorKeywords.any { it in secondaryHost } ||
        !isValidOfficialDomain(secondaryHost, isGovSector)
      ) {
        return SourceVerificationResult.Blocked("Unverified PDF URL host: $secondaryHost")
      }
    }

    return SourceVerificationResult.Verified(
      domain = officialDomain.trim(),
      verifiedTimestamp = lastVerifiedTimestamp.trim(),
    )
  }

  private fun isOfficialGovHost(host: String): Boolean {
    return officialGovSuffixes.any { host.endsWith(it) } ||
      officialStatutoryGovHosts.any { host == it || host.endsWith(".$it") }
  }

  private fun isCorporateHost(host: String): Boolean {
    return verifiedCorporateHosts.any { host == it || host.endsWith(".$it") }
  }

  private fun hostsAlign(domainHost: String, urlHost: String): Boolean {
    val normDomain = domainHost.removePrefix("www.")
    val normUrl = urlHost.removePrefix("www.")
    return normDomain == normUrl ||
      normUrl.endsWith(".$normDomain") ||
      normDomain.endsWith(".$normUrl")
  }

  private fun extractHttpsHost(rawUrl: String): String? {
    return try {
      val uri = URI(rawUrl.trim())
      if (!uri.scheme.equals("https", ignoreCase = true)) return null
      uri.host?.lowercase()?.takeIf { it.isNotBlank() }
    } catch (_: Exception) {
      null
    }
  }

  private fun extractHost(rawUrl: String): String? {
    return try {
      URI(rawUrl.trim()).host?.lowercase()
    } catch (_: Exception) {
      null
    }
  }
}
