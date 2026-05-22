using UnityEngine;
using UnityEngine.UI;
using UnityEngine.EventSystems;

// Attach to each of the 4 move button prefabs in the Battle scene.
// Handles display, tooltip on hover, and disabled state.
public class MoveButtonUI : MonoBehaviour, IPointerEnterHandler, IPointerExitHandler
{
    [SerializeField] private Button button;
    [SerializeField] private Text moveNameText;
    [SerializeField] private GameObject tooltipPanel;
    [SerializeField] private Text tooltipText;

    private Move _move;
    private System.Action<string> _onClicked;

    public void Setup(Move move, System.Action<string> onClicked)
    {
        _move = move;
        _onClicked = onClicked;

        moveNameText.text = move.name;
        button.onClick.RemoveAllListeners();
        button.onClick.AddListener(() => _onClicked?.Invoke(_move.id));

        if (tooltipPanel != null) tooltipPanel.SetActive(false);
    }

    public void SetInteractable(bool interactable)
    {
        button.interactable = interactable;
    }

    public void OnPointerEnter(PointerEventData eventData)
    {
        if (_move == null || tooltipPanel == null) return;
        tooltipPanel.SetActive(true);
        tooltipText.text = string.IsNullOrEmpty(_move.description)
            ? BuildAutoDescription()
            : _move.description;
    }

    public void OnPointerExit(PointerEventData eventData)
    {
        if (tooltipPanel != null) tooltipPanel.SetActive(false);
    }

    private string BuildAutoDescription()
    {
        var sb = new System.Text.StringBuilder();
        sb.AppendLine(_move.type + " | " + _move.effect);
        if (_move.baseValue > 0) sb.AppendLine("Base value: " + _move.baseValue);
        if (_move.durationTurns > 0) sb.AppendLine("Duration: " + _move.durationTurns + " turns");
        return sb.ToString().TrimEnd();
    }
}
