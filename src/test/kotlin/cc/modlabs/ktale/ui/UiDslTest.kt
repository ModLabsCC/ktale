package cc.modlabs.ktale.ui

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class UiDslTest : FunSpec({
    test("UiPath generates common property paths") {
        UiPath.value("#PrefixField") shouldBe "#PrefixField.Value"
        UiPath.text("#PreviewName") shouldBe "#PreviewName.Text"
        UiPath.visible("#HelpContent") shouldBe "#HelpContent.Visible"
        UiPath.styleTextColor("#PreviewPrefix") shouldBe "#PreviewPrefix.Style.TextColor"
        UiPath.checkboxValue("#PrefixBold") shouldBe "#PrefixBold #CheckBox.Value"
    }

    test("UiEventKey.data prefixes with @ when missing") {
        UiEventKey.data("Prefix") shouldBe "@Prefix"
        UiEventKey.data("@Nickname") shouldBe "@Nickname"
    }
})

