using UnityEngine;
using UnityEngine.UI;

// Reusable component - attach to both HeroPanel and MonsterPanel in the Battle scene.
// Displays name, HP text and HP bar for one combatant.
public class CombatantUI : MonoBehaviour
{
    [SerializeField] private Text nameText;
    [SerializeField] private Text hpText;
    [SerializeField] private Slider hpBar;
    [SerializeField] private Text activeEffectsText;

    public void Setup(string combatantName, int currentHp, int maxHp)
    {
        nameText.text = combatantName;
        Refresh(currentHp, maxHp);
    }

    public void Refresh(int currentHp, int maxHp)
    {
        hpText.text = currentHp + " / " + maxHp;
        hpBar.value = maxHp > 0 ? (float)currentHp / maxHp : 0f;
    }

    public void ShowEffects(EffectView[] effects)
    {
        if (activeEffectsText == null) return;
        if (effects == null || effects.Length == 0)
        {
            activeEffectsText.text = "";
            return;
        }
        var lines = new System.Text.StringBuilder();
        foreach (var e in effects)
            lines.AppendLine(e.type + " (" + e.turnsRemaining + " turns)");
        activeEffectsText.text = lines.ToString().TrimEnd();
    }
}
